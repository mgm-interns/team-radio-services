package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.RadioOAuth2ClientToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RadioOAuth2ClientTokenRepository extends MongoRepository<RadioOAuth2ClientToken, String> {
    RadioOAuth2ClientToken findByAuthenticationId(String authenticationId);
    void deleteByAuthenticationId(String authenticationId);
}
