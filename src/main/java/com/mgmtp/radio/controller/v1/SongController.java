package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.domain.station.PlayList;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.sdo.SongStatus;
import com.mgmtp.radio.service.station.SongService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
public class SongController extends BaseRadioController {
    public static final String BASE_URL = "/api/v1/station";

    private static ConcurrentHashMap<String, PlayList> compareList = new ConcurrentHashMap<>();

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @ApiOperation(
            value = "Get list history song",
            notes = "Returns list history song of station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully"),
            @ApiResponse(code = 200, message = "No message then there is error, connection close")
    })
    @GetMapping("/{stationId}/history")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ServerSentEvent<List<SongDTO>>> getListSongHistory(@PathVariable(value = "stationId") String stationId, @RequestParam(value = "limit") Integer limit) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(thisSecond -> Tuples.of(thisSecond, songService.getListSongByStationId(stationId), limit, songDTODateDescComparator))
                .map(createDataForGetListHistorySong);
    }

    private Function<Tuple4<Long, Flux<SongDTO>, Integer, Comparator<SongDTO>>, ServerSentEvent<List<SongDTO>>> createDataForGetListHistorySong =
            dataOfTuple -> ServerSentEvent.<List<SongDTO>>builder()
                    .event("fetch")
                    .id(Long.toString(dataOfTuple.getT1()))
                    .data(
                            dataOfTuple
                                    .getT2()
                                    .collectList()
                                    .block().stream().filter(songDTO -> songDTO.getStatus() != SongStatus.playing)
                                    .sorted(dataOfTuple.getT4())
                                    .filter(distinctUrl(SongDTO::getUrl))
                                    .limit(dataOfTuple.getT3())
                                    .collect(Collectors.toList()))
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

    @ApiOperation(
            value = "Get playlist and now playing",
            notes = "Returns playlist and now playing of station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully")
    })
    @GetMapping("/{stationId}/playList")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ServerSentEvent<PlayList>> getPlayListByStationId(@PathVariable("stationId") String stationId) {
        return songService.getPlayListByStationId(stationId)
                .flatMapMany(playList -> Flux.just(
                        ServerSentEvent.<PlayList>builder()
                                .event("fetch")
                                .data(playList)
                                .build()))
                .delayElements(Duration.ofSeconds(1))
                .delayElements(Duration.ofMillis(100))
                .skipWhile(playListServerSentEvent -> {
                    PlayList compare = compareList.get(stationId);
                    if (compare == null || !compare.toString().equals(playListServerSentEvent.data().toString())) {
                        compareList.put(stationId, playListServerSentEvent.data());
                        return false;
                    } else {
                        return true;
                    }
                })
                .repeat()
                .publish()
                .refCount();
    }

    @ApiOperation(
            value = "Add song to station playlist",
            notes = "Return a new song"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = SongDTO.class),
            @ApiResponse(code = 400, message = "Error in request parameters", response = RadioBadRequestException.class),
            @ApiResponse(code = 404, message = "Station or Song not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PostMapping("/{stationId}/{youTubeVideoId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SongDTO> addSong(
            @PathVariable String stationId,
            @PathVariable String youTubeVideoId,
            @RequestParam(value = "message", required = false) String message
    ) {
        log.info("POST /api/v1/song  - data: " + youTubeVideoId.toString());

        String userId = getCurrentUser().isPresent() ? getCurrentUser().get().getId() : null;
        return songService.addSongToStationPlaylist(stationId, youTubeVideoId, message, userId);
    }

    @ApiOperation(
            value = "UpVote a song in station playlist",
            notes = "Return updated song in station playlist"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = SongDTO.class),
            @ApiResponse(code = 400, message = "Error in request parameters", response = RadioBadRequestException.class),
            @ApiResponse(code = 404, message = "Station or Song not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PatchMapping("/{stationId}/{songId}/upVote")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SongDTO> upVoteSong(
            @PathVariable String stationId,
            @PathVariable String songId
    ) throws RadioException {
        log.info("POST /api/v1/song/" + stationId + "/upvote  - data: " + songId);

        if (getCurrentUser().isPresent()) {
            return songService.upVoteSongInStationPlaylist(stationId, songId, getCurrentUser().get().getId());
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }

    @ApiOperation(
            value = "Upvote a song in station playlist",
            notes = "Return updated song in station playlist"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = SongDTO.class),
            @ApiResponse(code = 400, message = "Error in request parameters", response = RadioBadRequestException.class),
            @ApiResponse(code = 404, message = "Station or Song not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PatchMapping("/{stationId}/{songId}/downVote")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SongDTO> downVoteSong(
            @PathVariable String stationId,
            @PathVariable String songId
    ) throws RadioException {
        log.info("POST /api/v1/song/" + stationId + "/downVote  - data: " + songId);

        if (getCurrentUser().isPresent()) {
            return songService
                    .downVoteSongInStationPlaylist(stationId, songId, getCurrentUser().get().getId());
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }
}
