package com.mgmtp.radio.aop;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.service.user.RecentStationService;
import com.mgmtp.radio.service.user.UserService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Aspect
@Component
public class RecentStationAspect {
    public static final int SEGMENT_NUMBER = 4; // according to uri /api/v1/stations/stationId
    private final RecentStationService recentStationService;
    private final StationService stationService;
    private final HttpServletRequest request;
    private final Constant constant;
    private final UserService userService;

    public RecentStationAspect(RecentStationService recentStationService, StationService stationService, HttpServletRequest request, Constant constant, UserService userService) {
        this.recentStationService = recentStationService;
        this.stationService = stationService;
        this.request = request;
        this.constant = constant;
        this.userService = userService;
    }

    @AfterReturning(
        value = "execution(* com.mgmtp.radio.service.station.SongServiceImpl.addSongToStationPlaylist(..)) || " +
            "execution(* com.mgmtp.radio.service.station.SongServiceImpl.downVoteSongInStationPlaylist(..)) || " +
            "execution(* com.mgmtp.radio.service.station.SongServiceImpl.upVoteSongInStationPlaylist(..)) || " +
            "execution(* com.mgmtp.radio.service.conversation.MessageServiceImpl.create(..))"
    )
    public void createRecentStationAfterAddSong() {
        String friendlyStationId = getSegmentOfURI(SEGMENT_NUMBER);
        Optional<Cookie> cookieOptional = Optional.ofNullable(WebUtils.getCookie(request, constant.getCookieId()));
        String cookieId = constant.getDefaultCookie();
        if (cookieOptional.isPresent()) {
            cookieId = cookieOptional.get().getValue();
        }
        User user = userService.getAccessUser(cookieId);
        createRecentStation(user.getId(), getStationIdFromFriendlyId(friendlyStationId));
    }

    private void createRecentStation(String userId, String stationId){
        if (!checkIfExistRecentStation(userId, stationId)) {
            recentStationService.createRecentStation(userId, stationId).subscribe();
        }
    }

    private boolean checkIfExistRecentStation(String userId, String stationId) {
        return recentStationService.existsByUserIdAndStationId(userId, stationId);
    }

    private String getStationIdFromFriendlyId(String friendlyStationId){
        Optional<Station> station = stationService.retrieveByIdOrFriendlyId(friendlyStationId).blockOptional();
        if (!station.isPresent()) {
            throw new RadioNotFoundException("Can not find station");
        }
        return station.get().getId();
    }

    private String getSegmentOfURI(int segmentNumber){
        String uri = request.getRequestURI();
        String[] uriSplit = uri.split("/");
        return uriSplit[segmentNumber];
    }
}
