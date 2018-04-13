package com.mgmtp.radio.controller.v1;

import com.cloudinary.utils.StringUtils;
import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.service.station.StationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Log4j2
@RestController
@RequestMapping(StationController.BASE_URL)
public class StationController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/stations";

    private final StationService stationService;
    private final UserMapper userMapper;
    private final Constant constant;

    public StationController(StationService stationService, UserMapper userMapper, Constant constant) {
        this.stationService = stationService;
        this.userMapper = userMapper;
        this.constant = constant;
    }

    @ApiOperation(
            value = "GET all station",
            notes = "Returns all station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<StationDTO> getAllStation() {
        return this.stationService.getAll();
    }

    @ApiOperation(
            value = "GET station by id",
            notes = "Returns current station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<StationDTO> getStation(@PathVariable(value = "id") String stationId) throws RadioNotFoundException {
        return this.stationService.findById(stationId);
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
    public Mono<StationDTO> createStation(@Valid @RequestBody StationDTO stationDTO) throws RadioException {
        if(StringUtils.isEmpty(stationDTO.getId())) {
            throw new RadioBadRequestException("station id is invalid");
        }

        String userId = getCurrentUser().isPresent() ? getCurrentUser().get().getId() : null;

        return stationService.create(userId, stationDTO);
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
                                          @Valid @RequestBody final StationDTO stationDTO) {
        return stationService.update(id, stationDTO);
    }

}
