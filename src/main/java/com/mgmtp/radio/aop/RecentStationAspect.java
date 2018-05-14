package com.mgmtp.radio.aop;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.service.user.RecentStationService;
import com.mgmtp.radio.support.CookieHelper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Aspect
@Component
public class RecentStationAspect {
    public static final int SEGMENT_NUMBER = 4; // according to uri /api/v1/stations/stationId
    private final RecentStationService recentStationService;
    private final StationService stationService;
    private final HttpServletRequest request;
    private final CookieHelper cookieHelper;

    public RecentStationAspect(RecentStationService recentStationService, StationService stationService, HttpServletRequest request, CookieHelper cookieHelper) {
        this.recentStationService = recentStationService;
        this.stationService = stationService;
        this.request = request;
        this.cookieHelper = cookieHelper;
    }

    @AfterReturning(
        value = "execution(* com.mgmtp.radio.service.station.SongServiceImpl.addSongToStationPlaylist(..)) || " +
            "execution(* com.mgmtp.radio.service.station.SongServiceImpl.downVoteSongInStationPlaylist(..)) || " +
            "execution(* com.mgmtp.radio.service.station.SongServiceImpl.upVoteSongInStationPlaylist(..)) || " +
            "execution(* com.mgmtp.radio.service.conversation.MessageServiceImpl.create(..))"
    )
    public void createRecentStationAfterAddSong() {
        String friendlyStationId = getSegmentOfURI(SEGMENT_NUMBER);
        User user = cookieHelper.getUserWithCookie();
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
