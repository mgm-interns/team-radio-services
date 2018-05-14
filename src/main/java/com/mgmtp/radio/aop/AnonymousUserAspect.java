package com.mgmtp.radio.aop;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.service.user.UserService;
import com.mgmtp.radio.support.CookieHelper;
import com.mgmtp.radio.support.MappingAnonymousHelper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
@Aspect
public class AnonymousUserAspect {

    private final Constant constant;
    private final CookieHelper cookieHelper;
    private final UserService userService;
    private final MappingAnonymousHelper mappingAnonymousHelper;

    public AnonymousUserAspect(Constant constant, CookieHelper cookieHelper, UserService userService, MappingAnonymousHelper mappingAnonymousHelper) {
        this.constant = constant;
        this.cookieHelper = cookieHelper;
        this.userService = userService;
        this.mappingAnonymousHelper = mappingAnonymousHelper;
    }


    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.getUserById(..))", returning = "userInfo")
    public void mapAnonymousUser(UserDTO userInfo) {
        if (!constant.getDefaultCookie().equals(cookieHelper.getCookieId())) {
            User anonymousUser = userService.getAnonymousUser(cookieHelper.getCookieId());
            mappingAnonymousHelper.updateUserId(anonymousUser.getId(), userInfo.getId(), "ownerId", Station.class);
            userService.deleteById(anonymousUser.getId());

            // delete cookie
            cookieHelper.resetCookieId();
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
            Cookie cookie = new Cookie(constant.getCookieId(), null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
