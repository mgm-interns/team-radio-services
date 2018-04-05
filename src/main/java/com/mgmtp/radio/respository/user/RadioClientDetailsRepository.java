package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.RadioClientDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RadioClientDetailsRepository extends MongoRepository<RadioClientDetails, String> {
    RadioClientDetails findByClientId(String clientId);
    void deleteByClientId(String clientId);
}
