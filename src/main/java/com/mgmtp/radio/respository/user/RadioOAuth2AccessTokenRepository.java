package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.RadioOAuth2AccessToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RadioOAuth2AccessTokenRepository extends MongoRepository<RadioOAuth2AccessToken, String> {
    RadioOAuth2AccessToken findByTokenId(String tokenId);
    RadioOAuth2AccessToken findByAuthenticationId(String authenticationId);
    List<RadioOAuth2AccessToken> findByUsernameAndClientId(String username, String clientId);
    List<RadioOAuth2AccessToken> findByClientId(String clientId);
    void deleteByRefreshToken(String refreshToken);
    void deleteByTokenId(String tokenId);
}
