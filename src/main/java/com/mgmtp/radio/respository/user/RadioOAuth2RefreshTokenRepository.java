package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.RadioOAuth2RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RadioOAuth2RefreshTokenRepository extends MongoRepository<RadioOAuth2RefreshToken, String> {
    RadioOAuth2RefreshToken findByTokenId(String tokenId);
    void deleteByTokenId(String tokenId);
}
