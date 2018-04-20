package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.user.RecentStationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(RecentStationController.BASE_URL)
public class RecentStationController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/user/me/recent-station";
    private final RecentStationService recentStationService;

    public RecentStationController(RecentStationService recentStationService) {
        this.recentStationService = recentStationService;
    }

    @GetMapping
    public Flux<StationDTO> getRecentStation() throws RadioNotFoundException {
        String userId = getCurrentUser().isPresent() ? getCurrentUser().get().getId() : null;
        if(userId != null) {
            return recentStationService.getRecentStation(userId);
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }
}
