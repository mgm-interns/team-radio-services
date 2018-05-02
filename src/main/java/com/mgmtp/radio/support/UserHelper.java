package com.mgmtp.radio.support;

import com.mgmtp.radio.domain.conversation.Sender;
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

    public Sender convertUserToSender(User user) {
        Sender sender = new Sender();
        sender.setUserId(user.getId());
        sender.setUsername(user.getName());
        sender.setAvatarUrl(user.getAvatarUrl());
        return sender;
    }
}
