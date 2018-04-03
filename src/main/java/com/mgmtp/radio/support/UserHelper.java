package com.mgmtp.radio.support;

import com.mgmtp.radio.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserHelper {

    @Value("${user.type.anonymous}")
    private String userType;

    public Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = Optional.empty();
        if (!principal.equals(userType)) {
            user = Optional.of((User) principal);
        }
        return user;
    }

    public User convertUserToUserConversation(com.mgmtp.radio.domain.user.User user) {
        User userConversation = new User();
        userConversation.setId(user.getId());
        userConversation.setUsername(user.getUsername());
        userConversation.setAvatarUrl(user.getAvatarUrl());
        return userConversation;
    }
}
