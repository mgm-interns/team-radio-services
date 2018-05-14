package com.mgmtp.radio.support;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.service.user.UserService;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
@Data
public class CookieHelper {

    private final HttpServletRequest request;
    private final UserService userService;
    private final Constant constant;
    private final UserHelper userHelper;

    private String cookieId;

    public CookieHelper(HttpServletRequest request, UserService userService, Constant constant, UserHelper userHelper) {
        this.request = request;
        this.userService = userService;
        this.constant = constant;
        this.userHelper = userHelper;
        cookieId = constant.getDefaultCookie();
    }

    public User getUserWithCookie() {
        Optional<Cookie> cookie = Optional.ofNullable(WebUtils.getCookie(request, constant.getCookieId()));
        if (cookie.isPresent()) {
            this.cookieId = cookie.get().getValue();
        }
        User user = userService.getAccessUser(this.cookieId);
        return user;
    }

    public void resetCookieId() {
        this.cookieId = constant.getDefaultCookie();
    }
}
