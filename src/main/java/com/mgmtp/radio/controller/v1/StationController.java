package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.sdo.CloudinaryDataKeys;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.support.CloudinaryHelper;
import com.mgmtp.radio.support.ContentTypeValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(StationController.BASE_URL)
public class StationController extends BaseRadioController {
//
    public static final String BASE_URL = "/api/v1/stations";

    private StationRepository stationRepository;

    private final StationService stationService;
    private final CloudinaryHelper cloudinaryHelper;
    private final ContentTypeValidator contentTypeValidator;
    private final StationMapper stationMapper;

    public StationController(StationRepository stationRepository, StationMapper stationMapper, StationService stationService, CloudinaryHelper cloudinaryHelper, ContentTypeValidator contentTypeValidator) {
        this.stationService = stationService;
        this.cloudinaryHelper = cloudinaryHelper;
        this.contentTypeValidator = contentTypeValidator;
        this.stationMapper = stationMapper;
        this.stationRepository = stationRepository;

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
    public Flux<ResponseEntity<StationDTO>> getAllStation() {
        return this.stationService.getStations()
                .map(station -> ResponseEntity.status(HttpStatus.OK).body(station));
    }

    @ApiOperation(
            value = "GET station by id",
            notes = "Returns current station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<StationDTO>> getStation(@PathVariable(value = "id") String stationId) throws RadioNotFoundException {
        return this.stationService.getStation(stationId)
                .map((station) -> ResponseEntity.ok(station))
                .defaultIfEmpty(ResponseEntity.notFound().build());
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
    public Mono<ResponseEntity<StationDTO>> createStation(@Valid @RequestBody StationDTO stationDTO) {
        return stationService.createStation("string", stationDTO)
                .map(station -> ResponseEntity.status(HttpStatus.CREATED).body(station));
    }

    @ApiOperation(
            value = "Update the curren station",
            notes = "return updated station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in station is not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PutMapping("{id}")
    public Mono<ResponseEntity<StationDTO>> updateStation(@PathVariable(value = "id") final String id,
                                                   @Valid @RequestBody final StationDTO stationDTO) {
        return stationService.updateStation(id, stationDTO)
                .map(updatedStation -> new ResponseEntity<>(updatedStation, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
