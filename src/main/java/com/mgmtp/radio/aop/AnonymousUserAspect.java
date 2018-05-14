package com.mgmtp.radio.aop;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.service.user.UserService;
import com.mgmtp.radio.support.CookieHelper;
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
    private final StationService stationService;
    private final UserService userService;

    public AnonymousUserAspect(Constant constant, CookieHelper cookieHelper, StationService stationService, UserService userService) {
        this.constant = constant;
        this.cookieHelper = cookieHelper;
        this.stationService = stationService;
        this.userService = userService;
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.getUserById(..))", returning = "userInfo")
    public void mapAnonymousUser(UserDTO userInfo) {
        if (!constant.getDefaultCookie().equals(cookieHelper.getCookieId())) {
            User anonymousUser = userService.getAnonymousUser(cookieHelper.getCookieId());
            stationService.updateOwnerId(anonymousUser.getId(), userInfo.getId());
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
