package com.mgmtp.radio.security;

import com.mgmtp.radio.domain.user.RadioOAuth2AccessToken;
import com.mgmtp.radio.domain.user.RadioOAuth2RefreshToken;
import com.mgmtp.radio.respository.user.RadioOAuth2AccessTokenRepository;
import com.mgmtp.radio.respository.user.RadioOAuth2RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class RadioTokenStore implements TokenStore {

    private final RadioOAuth2AccessTokenRepository radioOAuth2AccessTokenRepository;

    private final RadioOAuth2RefreshTokenRepository radioOAuth2RefreshTokenRepository;

    private final AuthenticationKeyGenerator authenticationKeyGenerator;

    @Autowired
    public RadioTokenStore(RadioOAuth2AccessTokenRepository mongoOAuth2AccessTokenRepository,
                           RadioOAuth2RefreshTokenRepository mongoOAuth2RefreshTokenRepository,
                           AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.radioOAuth2AccessTokenRepository = mongoOAuth2AccessTokenRepository;
        this.radioOAuth2RefreshTokenRepository = mongoOAuth2RefreshTokenRepository;
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken oAuth2AccessToken) {
        return readAuthentication(oAuth2AccessToken.getValue());    }

    @Override
    public OAuth2Authentication readAuthentication(final String token) {
        final String tokenId = extractTokenKey(token);

        final RadioOAuth2AccessToken mongoOAuth2AccessToken = radioOAuth2AccessTokenRepository.findByTokenId(tokenId);

        if (mongoOAuth2AccessToken != null) {
            try {
                return deserializeAuthentication(mongoOAuth2AccessToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                removeAccessToken(token);
            }
        }

        return null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (readAccessToken(token.getValue())!=null) {
            removeAccessToken(token.getValue());
        }

        final String tokenKey = extractTokenKey(token.getValue());

        final RadioOAuth2AccessToken oAuth2AccessToken = new RadioOAuth2AccessToken(tokenKey,
                serializeAccessToken(token),
                authenticationKeyGenerator.extractKey(authentication),
                authentication.isClientOnly() ? null : authentication.getName(),
                authentication.getOAuth2Request().getClientId(),
                serializeAuthentication(authentication),
                extractTokenKey(refreshToken));

        radioOAuth2AccessTokenRepository.save(oAuth2AccessToken);
    }

    public void removeAccessToken(final String tokenValue) {
        final String tokenKey = extractTokenKey(tokenValue);
        radioOAuth2AccessTokenRepository.deleteByTokenId(tokenKey);
    }

    @Override
    public OAuth2AccessToken readAccessToken(final String tokenValue) {
        final String tokenKey = extractTokenKey(tokenValue);
        final RadioOAuth2AccessToken mongoOAuth2AccessToken = radioOAuth2AccessTokenRepository.findByTokenId(tokenKey);
        if (mongoOAuth2AccessToken != null) {
            try {
                return deserializeAccessToken(mongoOAuth2AccessToken.getToken());
            } catch (IllegalArgumentException e) {
                removeAccessToken(tokenValue);
            }
        }
        return null;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        removeAccessToken(oAuth2AccessToken.getValue());
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication oAuth2Authentication) {
        final String tokenKey = extractTokenKey(refreshToken.getValue());
        final byte[] token = serializeRefreshToken(refreshToken);
        final byte[] authentication = serializeAuthentication(oAuth2Authentication);

        final RadioOAuth2RefreshToken oAuth2RefreshToken = new RadioOAuth2RefreshToken(tokenKey, token, authentication);

        radioOAuth2RefreshTokenRepository.save(oAuth2RefreshToken);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        final String tokenKey = extractTokenKey(tokenValue);
        final RadioOAuth2RefreshToken mongoOAuth2RefreshToken = radioOAuth2RefreshTokenRepository.findByTokenId(tokenKey);

        if (mongoOAuth2RefreshToken != null) {
            try {
                return deserializeRefreshToken(mongoOAuth2RefreshToken.getToken());
            } catch (IllegalArgumentException e) {
                removeRefreshToken(tokenValue);
            }
        }

        return null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        return readAuthenticationForRefreshToken(oAuth2RefreshToken.getValue());
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        removeRefreshToken(oAuth2RefreshToken.getValue());
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        removeAccessTokenUsingRefreshToken(oAuth2RefreshToken.getValue());
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;

        String key = authenticationKeyGenerator.extractKey(authentication);

        final RadioOAuth2AccessToken oAuth2AccessToken = radioOAuth2AccessTokenRepository.findByAuthenticationId(key);

        if (oAuth2AccessToken != null) {
            accessToken = deserializeAccessToken(oAuth2AccessToken.getToken());
        }

        if (accessToken != null
                && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken.getValue());
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String username, String clientId) {
        final List<RadioOAuth2AccessToken> oAuth2AccessTokens = radioOAuth2AccessTokenRepository.findByUsernameAndClientId(username, clientId);

        final Collection<RadioOAuth2AccessToken> noNullsTokens = oAuth2AccessTokens.stream()
                .filter(byNotNulls())
                .collect(Collectors.toList());

        return noNullsTokens.stream()
                .map(toOAuth2AccessToken())
                .collect(Collectors.toList());
    }

    private Predicate<RadioOAuth2AccessToken> byNotNulls() {
        return radioOAuth2AccessToken -> (radioOAuth2AccessToken != null);
    }

    private Function<RadioOAuth2AccessToken, OAuth2AccessToken> toOAuth2AccessToken() {
        return radioOAuth2AccessToken -> SerializationUtils.deserialize(radioOAuth2AccessToken.getToken());
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        final List<RadioOAuth2AccessToken> radioOAuth2AccessTokens = radioOAuth2AccessTokenRepository.findByClientId(clientId);

        final Collection<RadioOAuth2AccessToken> noNullTokens = radioOAuth2AccessTokens.stream()
                .filter(byNotNulls())
                .collect(Collectors.toList());

        return noNullTokens.stream()
                .map(toOAuth2AccessToken())
                .collect(Collectors.toList());
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

    protected byte[] serializeAccessToken(OAuth2AccessToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return SerializationUtils.serialize(authentication);
    }

    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return SerializationUtils.deserialize(authentication);
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
        final String tokenId = extractTokenKey(value);

        final RadioOAuth2RefreshToken radioOAuth2RefreshToken = radioOAuth2RefreshTokenRepository.findByTokenId(tokenId);

        if (radioOAuth2RefreshToken != null) {
            try {
                return deserializeAuthentication(radioOAuth2RefreshToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                removeRefreshToken(value);
            }
        }

        return null;
    }

    public void removeRefreshToken(String token) {
        final String tokenId = extractTokenKey(token);
        radioOAuth2RefreshTokenRepository.deleteByTokenId(tokenId);
    }

    public void removeAccessTokenUsingRefreshToken(final String refreshToken) {
        final String tokenId = extractTokenKey(refreshToken);
        radioOAuth2AccessTokenRepository.deleteByRefreshToken(tokenId);

    }
}
