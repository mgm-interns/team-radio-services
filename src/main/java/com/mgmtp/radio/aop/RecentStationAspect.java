package com.mgmtp.radio.aop;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.service.user.RecentStationService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Aspect
@Component
public class RecentStationAspect {
    private final RecentStationService recentStationService;
    private final StationService stationService;
    private final HttpServletRequest request;

    public RecentStationAspect(RecentStationService recentStationService, StationService stationService, HttpServletRequest request) {
        this.recentStationService = recentStationService;
        this.stationService = stationService;
        this.request = request;
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.station.SongServiceImpl.addSongToStationPlaylist(..))")
    public void createRecentStationAfterAddSong() {
        System.out.println("addSong");
        String url = request.getRequestURI();
        String[] urlSplit = url.split("/");
        String stationId = urlSplit[urlSplit.length - 2];
        createRecentStation(getCurrentUserId(), stationId);
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.station.SongServiceImpl.downVoteSongInStationPlaylist(..))")
    public void createRecentStationAfterDownVote() {
        String url = request.getRequestURI();
        String[] urlSplit = url.split("/");
        String stationId = urlSplit[urlSplit.length - 3];
        createRecentStation(getCurrentUserId(), stationId);
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.station.SongServiceImpl.upVoteSongInStationPlaylist(..))")
    public void createRecentStationAfterUpvote() {
        String url = request.getRequestURI();
        String[] urlSplit = url.split("/");
        String stationId = urlSplit[urlSplit.length - 3];
        createRecentStation(getCurrentUserId(), stationId);
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.conversation.MessageServiceImpl.create(..))")
    public void createRecentStationAfterSendMessage() {
        String url = request.getRequestURI();
        String[] urlSplit = url.split("/");
        String stationId = urlSplit[urlSplit.length - 2];
        createRecentStation(getCurrentUserId(), stationId);
    }

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = Optional.empty();
        if(!principal.equals("anonymousUser")) {
            user = Optional.of((User) principal);
        }
        return user.get().getId();
    }

    private void createRecentStation(String userId, String stationId){
        if(!checkIfExistRecentStation(userId, stationId)) {
            recentStationService.createRecentStation(userId, stationId).subscribe();
        }
    }

    private boolean checkIfExistRecentStation(String userId, String stationId) {
        return recentStationService.existsByUserIdAndStationId(userId, stationId);
    }
}
