package com.mgmtp.radio.controller.v1;

import com.cloudinary.utils.StringUtils;
import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.exception.RadioDuplicateNameException;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.support.validator.station.CreateStationValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(StationController.BASE_URL)
public class StationController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/stations";

    private final StationService stationService;
    private final UserMapper userMapper;
    private final Constant constant;
    private final CreateStationValidator createStationValidator;


    public StationController(StationService stationService, UserMapper userMapper, Constant constant,
                             CreateStationValidator createStationValidator) {
        this.stationService = stationService;
        this.userMapper = userMapper;
        this.constant = constant;
        this.createStationValidator = createStationValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(this.createStationValidator);
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
    public Map<String, StationDTO> getAllStation() {
            return this.stationService.getAllStationWithArrangement();
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
        return this.stationService.findById(stationId).switchIfEmpty(Mono.error(new RadioNotFoundException("Station not found!")));
        if(getCurrentUser().isPresent()) {
            return this.stationService.joinStation(stationId,userMapper.userToUserDTO(getCurrentUser().get()));
        }else{
            UserDTO anonymousUserDto = new UserDTO();
            return this.stationService.joinStation(stationId,anonymousUserDto);
        }
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
    public Mono<StationDTO> createStation(@Validated @RequestBody StationDTO stationDTO, BindingResult bindingResult) throws RadioException {
        String userId = getCurrentUser().isPresent() ? getCurrentUser().get().getId() : null;
        if(bindingResult.hasErrors()) {
            return Mono.error(new RadioDuplicateNameException(stationDTO.getName()));
        }
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
                                          @Validated @RequestBody final StationDTO stationDTO) {
        return stationService.update(id, stationDTO);
    }

    @PutMapping("/update-config/{id}")
    public Mono<StationConfigurationDTO> updateConfigurationStation(@PathVariable(value = "id") final String id,
                                                                    @RequestBody final StationConfigurationDTO stationConfigurationDTO) {
        return stationService.updateConfiguration(id, stationConfigurationDTO);
    }
}
