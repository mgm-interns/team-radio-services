package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findFirstByEmail(String email);
    User findByUsernameAndPassword(String username, String password);
    List<User> findByIdIn(List<String> userIds);
    Optional<User> findFirstByFacebookId(String facebookId);
    Optional<User> findFirstByGoogleId(String googleId);
    List<User> findByUpdatedAtEquals(LocalDate date);
    Optional<User> findByIdAndPassword(String userId, String password);
    Optional<User> findByResetPasswordTokenAndResetPasswordTokenExpiryDateAfter(String resetPasswordToken, LocalDate resetPasswordDate);
    Optional<User> findByCookieId(String cookieId);
    void deleteById(String id);
}
