package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.domain.station.PlayList;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.HistoryDTO;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.sdo.HistoryLimitation;
import com.mgmtp.radio.service.station.HistoryService;
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.service.station.StationOnlineService;
import com.mgmtp.radio.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@RestController
@RequestMapping(SongController.BASE_URL)
public class SongController extends BaseRadioController {
    public static final String BASE_URL = "/api/v1/station";
    private static ConcurrentHashMap<String, Flux<ServerSentEvent<PlayList>>> stationStream = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Integer> compareHash = new ConcurrentHashMap<>();

    private final SongService songService;
    private final HistoryService historyService;
    private final UserService userService;
    private final StationOnlineService stationOnlineService;
    private final UserMapper userMapper;

    @Value("${user.type.anonymous.cookie}")
    private String defaultCookie;

    @Value("${user.type.anonymous.cookie.key}")
    private String cookieId;

    public SongController(SongService songService,
                          HistoryService historyService,
                          UserService userService,
                          StationOnlineService stationOnlineService,
                          UserMapper userMapper) {
        this.songService = songService;
        this.historyService = historyService;
        this.userService = userService;
        this.stationOnlineService = stationOnlineService;
        this.userMapper = userMapper;
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
    public Flux<HistoryDTO> getListSongHistory(@PathVariable(value = "stationId") String stationId) {
        return historyService.getHistoryByStationId(stationId)
                .take(HistoryLimitation.first.getLimit());
    }

    @ApiOperation(
            value = "Get playlist and now playing",
            notes = "Returns playlist and now playing of station"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully")
    })
    @GetMapping("/{stationId}/playList")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ServerSentEvent<PlayList>> getPlayListByStationId(@PathVariable("stationId") String stationId,
                                                                  HttpServletResponse response,
                                                                  @CookieValue(value = "cookieId", defaultValue = "defaultCookie") String cookieId) {
        Flux<ServerSentEvent<PlayList>> stationPlayListStream = stationStream.get(stationId);
        if (stationPlayListStream == null) {
            long[] currentTimetamp = new long[]{0};
            long[] lastDataTick = new long[]{0};
            User[] user = new User[1];
            stationPlayListStream =
                    Flux.interval(Duration.ofMillis(1100)).map(tick -> Tuples.of(tick, songService.getPlayListByStationId(stationId, currentTimetamp[0])))
                            .map(data -> data.getT2().map(playList -> {
                                        int currentHash = playList.hashCode();
                                        Optional<Integer> previousHash = Optional.ofNullable(compareHash.get(stationId));
                                        currentTimetamp[0] = 0;
                                        if (!previousHash.isPresent() || previousHash.get() != currentHash || (data.getT1() - lastDataTick[0]) >= 2 ) {
                                            lastDataTick[0] = data.getT1();
                                            compareHash.put(stationId, currentHash);
                                            return ServerSentEvent.<PlayList>builder().id(Long.toString(data.getT1())).event("fetch").data(playList).build();
                                        } else {
                                            return ServerSentEvent.<PlayList>builder().build();
                                        }
                                    })
                            )
                            .flatMap(Flux::from)
                            .publish()
                            .refCount()
                            .doOnSubscribe(subscription -> {
                                compareHash.remove(stationId);
                                currentTimetamp[0] = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
                                user[0] = userService.getAccessUser(cookieId);
                                if (!getCurrentUser().isPresent() && defaultCookie.equals(cookieId)) {
                                    Cookie cookie = new Cookie(cookieId, user[0].getCookieId());
                                    cookie.setPath("/");
                                    response.addCookie(cookie);
                                }

                            })
                            .doFinally(signalType -> {
                                stationOnlineService.removeOnlineUser(userMapper.userToUserDTO(user[0]), stationId);
                            });

            stationStream.put(stationId, stationPlayListStream);
        }
        return stationPlayListStream;
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
            @RequestBody(required = false) String message,
            @CookieValue(value = "cookieId", defaultValue = "defaultCookie") String cookieId
    ) {
        log.info("POST /api/v1/song  - data: " + youTubeVideoId.toString());

        User user = userService.getAccessUser(cookieId);
        return songService.addSongToStationPlaylist(stationId, youTubeVideoId, message, user);
    }

    @ApiOperation(
            value = "Up vote a song in station playlist",
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
            value = "Down vote a song in station playlist",
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
