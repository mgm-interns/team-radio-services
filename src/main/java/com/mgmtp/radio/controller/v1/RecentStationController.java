package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.user.RecentStationService;
import com.mgmtp.radio.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Optional;

@RestController
@RequestMapping(RecentStationController.BASE_URL)
public class RecentStationController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/user";

    private final UserService userService;
    private final RecentStationService recentStationService;

    public RecentStationController(UserService userService, RecentStationService recentStationService) {
        this.userService = userService;
        this.recentStationService = recentStationService;
    }

    @GetMapping("/me/recent-station")
    public Flux<StationDTO> getRecentStation() throws RadioNotFoundException {
        String userId = getCurrentUser().isPresent() ? getCurrentUser().get().getId() : null;
        if(userId != null) {
            return recentStationService.getRecentStation(userId);
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }

    @ApiOperation(
            value = "Get recent stations of a specific user",
            notes = "Returns the recent stations"
    )

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in user is not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping("/{userId}/recent-station")
    public Flux<StationDTO> getRecentStation(@PathVariable(value = "userId") String userId) throws RadioNotFoundException {
        Optional<UserDTO> user = Optional.ofNullable(userService.getUserById(userId));
        if(user.isPresent()) {
            return recentStationService.getRecentStationsByUserIdAndPrivacy(userId, StationPrivacy.station_public);
        } else {
            throw new RadioNotFoundException("user is not found");
        }
    }
}
