package com.mgmtp.radio.service.reputation;

import com.mgmtp.radio.domain.reputation.ReputationEventLog;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.respository.reputation.ReputationEventLogRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.sdo.ReputationEventKeys;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Service
public class ReputationServiceImpl implements ReputationService {

    private final ReputationEventLogRepository reputationEventLogRepository;
    private final UserRepository userRepository;
    private final int USER_UPDATE_AVATAR_POINT = 20;
    private final int USER_UPDATE_COVER_POINT = 2;
    private final int USER_UPDATE_INFO_POINT = 2;

    public ReputationServiceImpl(ReputationEventLogRepository reputationEventLogRepository,
                                 UserRepository userRepository) {
        this.reputationEventLogRepository = reputationEventLogRepository;
        this.userRepository = userRepository;
    }

    public User updateUserReputation(User user) {

        List<ReputationEventLog> reputationEventLogs = reputationEventLogRepository.findByUserId(user.getId())
                .toStream()
                .collect(Collectors.toList());

        if (!StringUtils.isEmpty(user.getAvatarUrl())
                && reputationEventLogs.stream().noneMatch(reputationEventLog -> reputationEventLog.getEvent() == ReputationEventKeys.user_update_avatar)) {

            ReputationEventLog reputationEventLog =
                    saveReputationEventLog(user.getId(), ReputationEventKeys.user_update_avatar, USER_UPDATE_AVATAR_POINT);
            reputationEventLogRepository.save(reputationEventLog);
        }

        if (!StringUtils.isEmpty(user.getCoverUrl())
                && reputationEventLogs.stream().noneMatch(reputationEventLog -> reputationEventLog.getEvent() == ReputationEventKeys.user_update_cover)) {

            ReputationEventLog reputationEventLog =
                    saveReputationEventLog(user.getId(), ReputationEventKeys.user_update_cover, USER_UPDATE_COVER_POINT);
            reputationEventLogRepository.save(reputationEventLog);
        }

        if (!StringUtils.isEmpty(user.getFirstName())
                && reputationEventLogs.stream().noneMatch(reputationEventLog -> reputationEventLog.getEvent() == ReputationEventKeys.user_update_first_name)) {

            ReputationEventLog reputationEventLog =
                    saveReputationEventLog(user.getId(), ReputationEventKeys.user_update_first_name, USER_UPDATE_INFO_POINT);
            reputationEventLogRepository.save(reputationEventLog);
        }

        if (!StringUtils.isEmpty(user.getLastName())
                && reputationEventLogs.stream().noneMatch(reputationEventLog -> reputationEventLog.getEvent() == ReputationEventKeys.user_update_last_name)) {

            ReputationEventLog reputationEventLog =
                    saveReputationEventLog(user.getId(), ReputationEventKeys.user_update_last_name, USER_UPDATE_INFO_POINT);
            reputationEventLogRepository.save(reputationEventLog);
        }

        if (!StringUtils.isEmpty(user.getCountry())
                && reputationEventLogs.stream().noneMatch(reputationEventLog -> reputationEventLog.getEvent() == ReputationEventKeys.user_update_country)) {

            ReputationEventLog reputationEventLog =
                    saveReputationEventLog(user.getId(), ReputationEventKeys.user_update_country, USER_UPDATE_INFO_POINT);
            reputationEventLogRepository.save(reputationEventLog);
        }

        if (!StringUtils.isEmpty(user.getCity())
                && reputationEventLogs.stream().noneMatch(reputationEventLog -> reputationEventLog.getEvent() == ReputationEventKeys.user_update_city)) {

            ReputationEventLog reputationEventLog =
                    saveReputationEventLog(user.getId(), ReputationEventKeys.user_update_city, USER_UPDATE_INFO_POINT);
            reputationEventLogRepository.save(reputationEventLog);
        }

        if (!StringUtils.isEmpty(user.getBio())
                && reputationEventLogs.stream().noneMatch(reputationEventLog -> reputationEventLog.getEvent() == ReputationEventKeys.user_update_bio)) {
            ReputationEventLog reputationEventLog =
                    saveReputationEventLog(user.getId(), ReputationEventKeys.user_update_bio, USER_UPDATE_INFO_POINT);

            reputationEventLogRepository.save(reputationEventLog);
        }

        Flux<ReputationEventLog> reputationEventLogFlux = reputationEventLogRepository.findByUserId(user.getId());
        int score = reputationEventLogFlux.toStream().mapToInt(ReputationEventLog::getScore).sum();
        user.setReputation(score);
        return userRepository.save(user);
    }

    private ReputationEventLog saveReputationEventLog(String userId, ReputationEventKeys reputationEventKey, int points) {
        ReputationEventLog reputationEventLog = new ReputationEventLog();
        reputationEventLog.setScore(points);
        reputationEventLog.setEvent(reputationEventKey);
        reputationEventLog.setUserId(userId);
        reputationEventLog.setCreateAt(LocalDateTime.now());
        return reputationEventLogRepository.save(reputationEventLog).block();
    }

}
