package com.mgmtp.radio.bootstrap;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.service.reputation.ReputationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Bootstrap implements CommandLineRunner {

    private UserRepository userRepository;
    private ReputationService reputationService;

    public Bootstrap(
            UserRepository userRepository,
            ReputationService reputationService) {
        this.userRepository = userRepository;
        this.reputationService = reputationService;
    }

    @Override
    public void run(String... args) throws Exception {
        updateReputation();
    }

    void updateReputation() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> reputationService.updateUserReputation(user));
    }
}
