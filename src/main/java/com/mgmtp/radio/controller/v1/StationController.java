package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.service.user.UserService;
import com.mgmtp.radio.support.validator.station.CreateStationValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(StationController.BASE_URL)
public class StationController extends BaseRadioController {
    private static Flux<Map<String, StationDTO>> allStationStream;
    public static final String BASE_URL = "/api/v1/stations";

    private final StationService stationService;
    private final UserMapper userMapper;
    private final Constant constant;
    private final CreateStationValidator createStationValidator;
    private final SubscribableChannel allStationChannel;
    private final UserService userService;
    private final SubscribableChannel onlineUserOnlineChannel;

    public StationController(StationService stationService, UserMapper userMapper, Constant constant,
                             CreateStationValidator createStationValidator, SubscribableChannel allStationChannel, UserService userService,
                             SubscribableChannel onlineUserOnlineChannel) {
        this.stationService = stationService;
        this.userMapper = userMapper;
        this.constant = constant;
        this.createStationValidator = createStationValidator;
        this.allStationChannel = allStationChannel;
        this.userService = userService;
        this.onlineUserOnlineChannel = onlineUserOnlineChannel;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(this.createStationValidator);
    }

    @ApiOperation(
            value = "GET all stations",
            notes = "Returns all stations"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<List<StationDTO>> getAllStationsStream(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "limit", defaultValue = "40") int limit) {
        return Flux.create(sink -> {
            MessageHandler messageHandler = message -> {
                Map<String, StationDTO> data = (Map<String, StationDTO>) message.getPayload();
                data = data.entrySet()
                        .stream()
                        .skip(page)
                        .limit(limit)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                sink.next(data);
            };
            sink.onCancel(() -> allStationChannel.unsubscribe(messageHandler));
            sink.onDispose(() -> allStationChannel.unsubscribe(messageHandler));
            allStationChannel.subscribe(messageHandler);
        })
        .map(mapper -> ((Map<String, StationDTO>) mapper).values().stream().collect(Collectors.toList()));
    }

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Map<String, StationDTO> getAllStation () {
		return this.stationService.getOrderedStations();
	}

    @ApiOperation(
            value = "GET station by id",
            notes = "Returns current station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<Map<String, Object>>  getStation(@PathVariable(value = "id") String stationId,
                                       HttpServletResponse response,
                                       @CookieValue(value = "cookieId", defaultValue = "defaultCookie") String cookieId
    ) throws RadioNotFoundException {
        User user = userService.getAccessUser(cookieId);
        if (!getCurrentUser().isPresent() && constant.getDefaultCookie().equals(cookieId)) {
            Cookie cookie = new Cookie(constant.getCookieId(), user.getCookieId());
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return Flux.create(sink -> {
            MessageHandler messageHandler = message -> {
                Map<String, Object> data = (Map<String, Object>) message.getPayload();
                StationDTO stationInfo = (StationDTO) data.get("stationInfo");
                if (stationId.equals(stationInfo.getFriendlyId())){
                    sink.next(data);
                }
            };
            sink.onDispose(() -> onlineUserOnlineChannel.unsubscribe(messageHandler));
            sink.onCancel(() -> onlineUserOnlineChannel.unsubscribe(messageHandler));
            onlineUserOnlineChannel.subscribe(messageHandler);
        });
    }

    @ApiOperation(
            value = "POST station",
            notes = "Create station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in request parameters", response = RadioBadRequestException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PostMapping
    public Mono<StationDTO> createStation(@Validated @RequestBody StationDTO stationDTO,
                                          BindingResult bindingResult,
                                          HttpServletResponse response,
                                          @CookieValue(value = "cookieId", defaultValue = "defaultCookie") String cookieId
    ) throws RadioException {
        if (bindingResult.hasErrors()) {
            return Mono.error(new RadioBadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        User user = userService.getAccessUser(cookieId);
        if (!getCurrentUser().isPresent()) {
            stationDTO.setPrivacy(StationPrivacy.station_private);
            if (constant.getDefaultCookie().equals(cookieId)) {
                Cookie cookie = new Cookie(constant.getCookieId(), user.getCookieId());
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        return stationService.create(user.getId(), stationDTO);
    }

    @ApiOperation(
            value = "Update the current station",
            notes = "return updated station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in station is not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PutMapping("/{id}")
    public Mono<StationDTO> updateStation(@PathVariable(value = "id") final String id,
                                          @Validated @RequestBody final StationDTO stationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.error(new RadioBadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        return stationService.update(id, stationDTO);
    }

    @PutMapping("/update-config/{id}")
    public Mono<StationConfigurationDTO> updateConfigurationStation(@PathVariable(value = "id") final String id,
                                                                    @RequestBody final StationConfigurationDTO stationConfigurationDTO) {
        return stationService.updateConfiguration(id, stationConfigurationDTO);
    }
}
