package com.mgmtp.radio.controller;

import com.mgmtp.radio.domain.user.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;

@Log4j2
public abstract class BaseRadioController {

    protected User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
