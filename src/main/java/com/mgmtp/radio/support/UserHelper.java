package com.mgmtp.radio.support;

import com.mgmtp.radio.domain.conversation.Sender;
import com.mgmtp.radio.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserHelper {

    @Value("${user.type.anonymous}")
    private String userType;

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal;
        if (authentication == null || !authentication.isAuthenticated()){
            principal = userType;
        } else {
             principal = authentication.getPrincipal();
        }
        Optional<User> user = Optional.empty();
        if (!principal.equals(userType)) {
            user = Optional.of((User) principal);
        }
        return user;
    }

    public Sender convertUserToSender(User user) {
        Sender sender = new Sender();
        sender.setUserId(user.getId());
        sender.setUsername(user.getName());
        sender.setAvatarUrl(user.getAvatarUrl());
        return sender;
    }
}
