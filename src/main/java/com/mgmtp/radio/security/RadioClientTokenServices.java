package com.mgmtp.radio.security;

import com.mgmtp.radio.domain.user.RadioOAuth2ClientToken;
import com.mgmtp.radio.respository.user.RadioOAuth2ClientTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientKeyGenerator;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RadioClientTokenServices implements ClientTokenServices {

    private final RadioOAuth2ClientTokenRepository radioOAuth2ClientTokenRepository;

    private final ClientKeyGenerator clientKeyGenerator;

    @Autowired
    public RadioClientTokenServices(RadioOAuth2ClientTokenRepository radioOAuth2ClientTokenRepository, ClientKeyGenerator clientKeyGenerator) {
        this.radioOAuth2ClientTokenRepository = radioOAuth2ClientTokenRepository;
        this.clientKeyGenerator = clientKeyGenerator;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails, Authentication authentication) {
        final RadioOAuth2ClientToken mongoOAuth2ClientToken =
                radioOAuth2ClientTokenRepository.findByAuthenticationId(clientKeyGenerator.extractKey(oAuth2ProtectedResourceDetails, authentication));
        return SerializationUtils.deserialize(mongoOAuth2ClientToken.getToken());
    }

    @Override
    public void saveAccessToken(OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails, Authentication authentication, OAuth2AccessToken oAuth2AccessToken) {
        removeAccessToken(oAuth2ProtectedResourceDetails, authentication);
        final RadioOAuth2ClientToken radioOAuth2ClientToken = new RadioOAuth2ClientToken(UUID.randomUUID().toString(),
                oAuth2AccessToken.getValue(),
                SerializationUtils.serialize(oAuth2AccessToken),
                clientKeyGenerator.extractKey(oAuth2ProtectedResourceDetails, authentication),
                authentication.getName(),
                oAuth2ProtectedResourceDetails.getClientId());

        radioOAuth2ClientTokenRepository.save(radioOAuth2ClientToken);
    }

    @Override
    public void removeAccessToken(OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails, Authentication authentication) {
        radioOAuth2ClientTokenRepository.deleteByAuthenticationId(clientKeyGenerator.extractKey(oAuth2ProtectedResourceDetails, authentication));
    }
}
