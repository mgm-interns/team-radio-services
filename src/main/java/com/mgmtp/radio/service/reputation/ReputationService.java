package com.mgmtp.radio.service.reputation;

import com.mgmtp.radio.dto.reputation.ReputationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import reactor.core.publisher.Mono;

public interface ReputationService {
    Mono<ReputationDTO> getReputation(String userId);

    void createReputationForUserRegister(UserDTO userDTO);

    void updateReputationForUserUpdateAvatar(UserDTO userDTO);

    void updateReputationForUserUpdateCover(UserDTO userDTO);

    void updateReputationForUserUpdateInfo(UserDTO userDTO);
}
