package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
}
