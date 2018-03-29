package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.service.station.SongService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping(SongController.BASE_URL)
public class SongController {
    public static final String BASE_URL = "/api/v1/songs";

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @ApiOperation(
            value = "Get list song",
            notes = "Returns list all song of station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully"),
            @ApiResponse(code = 200, message = "No message then there is error, connection close")
    })
    @GetMapping("/sse/listSong")
    public Flux<ServerSentEvent<RadioSuccessResponse<List<SongDTO>>>> getListSong(@RequestParam("stationId") String stationId) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(thisSecond -> Tuples.of(thisSecond, songService.getListSongByStationId(stationId)))
                .map(createDataForGetListSong);
    }

    private Function<Tuple2<Long, Flux<SongDTO>>, ServerSentEvent<RadioSuccessResponse<List<SongDTO>>>> createDataForGetListSong =
        dataOfTuple -> ServerSentEvent.<RadioSuccessResponse<List<SongDTO>>>builder()
            .event("fetch")
            .id(Long.toString(dataOfTuple.getT1()))
            .data(new RadioSuccessResponse<>(dataOfTuple.getT2().collectList().block()))
            .build();

    @ApiOperation(
            value = "Get list available song",
            notes = "Returns list available song of station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully"),
            @ApiResponse(code = 200, message = "No message then there is error, connection close")
    })
    @GetMapping("/sse/listAvailableSong")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ServerSentEvent<RadioSuccessResponse<List<SongDTO>>>> getAvailableListSong(@RequestParam("stationId") String stationId) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(thisSecond -> Tuples.of(thisSecond, songService.getListSongByStationId(stationId)))
                .map(createDataForGetAvailableListSong);
    }

    private Function<Tuple2<Long, Flux<SongDTO>>, ServerSentEvent<RadioSuccessResponse<List<SongDTO>>>> createDataForGetAvailableListSong =
        dataOfTuple -> ServerSentEvent.<RadioSuccessResponse<List<SongDTO>>>builder()
            .event("fetch")
            .id(Long.toString(    dataOfTuple.getT1()))
            .data(new RadioSuccessResponse<>(
                    dataOfTuple
                    .getT2()
                    .collectList()
                    .block()
                    .stream()
                    .filter(songDTO -> songDTO.isPlaying())
                    .collect(Collectors.toList())))
            .build();

    @ApiOperation(
            value = "Get list history song",
            notes = "Returns list history song of station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully"),
            @ApiResponse(code = 200, message = "No message then there is error, connection close")
    })
    @GetMapping("/sse/listHistorySong")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ServerSentEvent<RadioSuccessResponse<List<SongDTO>>>> getListSongHistory(@RequestParam(value = "stationId") String stationId, @RequestParam(value = "limit") Integer limit) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(thisSecond -> Tuples.of(thisSecond, songService.getListSongByStationId(stationId), limit, songDTODateDescComparator))
                .map(createDataForGetListHistorySong);
    }

    private Function<Tuple4<Long, Flux<SongDTO>, Integer, Comparator<SongDTO>>, ServerSentEvent<RadioSuccessResponse<List<SongDTO>>>> createDataForGetListHistorySong =
        dataOfTuple -> ServerSentEvent.<RadioSuccessResponse<List<SongDTO>>>builder()
            .event("fetch")
            .id(Long.toString(dataOfTuple.getT1()))
            .data(
                new RadioSuccessResponse<>(
                    dataOfTuple
                        .getT2()
                        .collectList()
                        .block().stream().filter(songDTO -> !songDTO.isPlaying())
                        .sorted(dataOfTuple.getT4())
                        .filter(distinctUrl(SongDTO::getUrl))
                        .limit(dataOfTuple.getT3())
                        .collect(Collectors.toList())))
            .build();

    private Predicate<SongDTO> distinctUrl(Function<SongDTO, String> getUrl) {
        Set<String> uniqueUrl = ConcurrentHashMap.newKeySet();
        return url -> uniqueUrl.add(getUrl.apply(url));
    }

    private Comparator<SongDTO> songDTODateDescComparator = (SongDTO song1, SongDTO song2) -> {
        if (song1.getCreatedAt().isAfter(song2.getCreatedAt())) {
            return -1;
        } else if (song1.getCreatedAt().isBefore(song2.getCreatedAt())) {
            return 1;
        }
        return 0;
    };
}
