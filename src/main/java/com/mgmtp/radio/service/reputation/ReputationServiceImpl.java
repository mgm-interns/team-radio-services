package com.mgmtp.radio.service.reputation;

import com.mgmtp.radio.domain.reputation.Reputation;
import com.mgmtp.radio.dto.reputation.ReputationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.reputation.ReputationMapper;
import com.mgmtp.radio.respository.reputation.ReputationRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Aspect
@Service
public class ReputationServiceImpl implements ReputationService{

    private final ReputationRepository reputationRepository;
    private ReputationMapper reputationMapper;
    private final int USER_UPDATE_AVATAR_POINT = 20;
    private final int USER_UPDATE_COVER_POINT = 2;
    private final int USER_UPDATE_INFO_POINT = 2;

    public ReputationServiceImpl(ReputationRepository reputationRepository, ReputationMapper reputationMapper) {
        this.reputationRepository = reputationRepository;
        this.reputationMapper = reputationMapper;
    }

    @Override
    public Mono<ReputationDTO> getReputation(String userId) {
        return reputationRepository.findByUserId(userId).map(
                reputation -> reputationMapper.reputationToReputationDTO(reputation))
                .switchIfEmpty(Mono.error(new RadioNotFoundException()));
    }

    @Override
    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.register(..))"
            , returning = "userDTO")
    public void createReputationForUserRegister(UserDTO userDTO) {
        if(userDTO != null) {
            Reputation reputation = new Reputation();
            reputation.setUserId(userDTO.getId());
            reputation.setScore(0);
            reputation.setUpdateAvatarAlready(false);
            reputationRepository.save(reputation).subscribe();
        }
    }

    @Override
    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.patchUserAvatar(..))"
            , returning = "userDTO")
    public void updateReputationForUserUpdateAvatar(UserDTO userDTO) {
        if(userDTO != null) {
            reputationRepository.findByUserId(userDTO.getId()).doOnNext(reputation -> {
                if (!reputation.isUpdateAvatarAlready()) {
                    reputation.setScore(reputation.getScore() + USER_UPDATE_AVATAR_POINT);
                    reputation.setUpdateAvatarAlready(true);
                    reputationRepository.save(reputation).subscribe();
                }
            }).subscribe();
        }
    }

    @Override
    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.patchUserCover(..))"
            , returning = "userDTO")
    public void updateReputationForUserUpdateCover(UserDTO userDTO) {
        if(userDTO != null) {
            reputationRepository.findByUserId(userDTO.getId()).map(reputation -> {
                reputation.setScore(reputation.getScore() + USER_UPDATE_COVER_POINT);
                return reputationRepository.save(reputation).subscribe();
            }).subscribe();
        }
    }

    @Override
    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.patchUser(..))"
            , returning = "userDTO")
    public void updateReputationForUserUpdateInfo(UserDTO userDTO) {
        if (userDTO != null) {
            reputationRepository.findByUserId(userDTO.getId()).map(reputation -> {
                reputation.setScore(reputation.getScore() + USER_UPDATE_INFO_POINT);
                return reputationRepository.save(reputation).subscribe();
            }).subscribe();
        }
    }
}
