package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.RadioApproval;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RadioApprovalRepository extends MongoRepository<RadioApproval, String> {
    List<RadioApproval> findByUserIdAndClientId(String userId, String clientId);
    Long deleteByUserIdAndClientIdAndScope(String userId, String clientId, String scope);
}
