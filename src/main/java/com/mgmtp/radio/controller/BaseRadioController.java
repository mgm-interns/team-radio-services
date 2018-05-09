package com.mgmtp.radio.controller;

import com.mgmtp.radio.domain.user.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Log4j2
public abstract class BaseRadioController {

    protected Optional<User> getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal;
        if (authentication == null || !authentication.isAuthenticated()){
            principal = "anonymousUser";
        } else {
            principal = authentication.getPrincipal();
        }
        Optional<User> user = Optional.empty();
        if(!principal.equals("anonymousUser")) {
            user = Optional.of((User) principal);
        }
        return user;
    }

}
