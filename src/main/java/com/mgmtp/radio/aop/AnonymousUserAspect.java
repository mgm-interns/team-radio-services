package com.mgmtp.radio.aop;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.RecentStation;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.service.station.StationOnlineService;
import com.mgmtp.radio.service.user.UserService;
import com.mgmtp.radio.support.MappingAnonymousHelper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
@Aspect
public class AnonymousUserAspect {

    private final Constant constant;
    private final UserService userService;
    private final MappingAnonymousHelper mappingAnonymousHelper;
    private final HttpServletRequest request;
    private final StationOnlineService stationOnlineService;

    public AnonymousUserAspect(Constant constant,
                               UserService userService,
                               MappingAnonymousHelper mappingAnonymousHelper,
                               HttpServletRequest request,
                               StationOnlineService stationOnlineService) {
        this.constant = constant;
        this.userService = userService;
        this.mappingAnonymousHelper = mappingAnonymousHelper;
        this.request = request;
        this.stationOnlineService = stationOnlineService;
    }

    private void mapAnonymousUserInAllStations(String anonymousUserId, String userId) {
        stationOnlineService.getAllStation()
            .values().stream()
            .filter(stationDTO -> anonymousUserId.equals(stationDTO.getOwnerId()))
            .forEach(stationDTO -> stationDTO.setOwnerId(userId));
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.getUserById(..))", returning = "userInfo")
    public void mapAnonymousUser(UserDTO userInfo) {
        Optional<Cookie> cookieOptional = Optional.ofNullable(WebUtils.getCookie(request, constant.getCookieId()));

        if (cookieOptional.isPresent()) {
            User anonymousUser = userService.getAnonymousUser(cookieOptional.get().getValue());

            mapAnonymousUserInAllStations(anonymousUser.getId(), userInfo.getId());
            mappingAnonymousHelper.updateUserId(anonymousUser.getId(), userInfo.getId(), "ownerId", Station.class);
            mappingAnonymousHelper.updateUserId(anonymousUser.getId(), userInfo.getId(), "userId", RecentStation.class);
            mappingAnonymousHelper.updateUserId(anonymousUser.getId(), userInfo.getId(), "creatorId", Song.class);
            userService.deleteById(anonymousUser.getId());

            // delete cookie
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
