package com.mgmtp.radio.service.station;

import com.google.api.services.youtube.model.Video;
import com.mgmtp.radio.config.YouTubeConfig;
import com.mgmtp.radio.domain.station.*;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.exception.SongNotFoundException;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.sdo.EventDataKeys;
import com.mgmtp.radio.sdo.SkipRuleType;
import com.mgmtp.radio.sdo.SongStatus;
import com.mgmtp.radio.sdo.SubscriptionEvents;
import com.mgmtp.radio.support.*;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service("songService")
public class SongServiceImpl implements SongService {
    private static final String BLANK = "";
    private static final double DOWN_VOTE_THRES_PERCENT = 0.5;
    private static final long TOTAL_TIME_SKIP = 5;

    private final StationRepository stationRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final YouTubeHelper youTubeHelper;
    private final TransferHelper transferHelper;
    private final DateHelper dateHelper;
    private final YouTubeConfig youTubeConfig;
    private final SongMapper songMapper;
    private final UserMapper userMapper;
    private final StationPlayerHelper stationPlayerHelper;
    private final MessageChannel historyChannel;
    private final StationSongSkipHelper stationSongSkipHelper;
    private final StationService stationService;
    private final MessageChannel shiftSongChannel;
    private final StationOnlineService stationOnlineService;

    public SongServiceImpl(
        SongMapper songMapper,
        UserMapper userMapper,
        SongRepository songRepository,
        StationRepository stationRepository,
        UserRepository userRepository,
        YouTubeHelper youTubeHelper,
        TransferHelper transferHelper,
        DateHelper dateHelper,
        YouTubeConfig youTubeConfig,
        StationPlayerHelper stationPlayerHelper,
        MessageChannel historyChannel,
        StationSongSkipHelper stationSongSkipHelper,
        StationService stationService,
        MessageChannel shiftSongChannel,
        StationOnlineService stationOnlineService) {
        this.songRepository = songRepository;
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.youTubeHelper = youTubeHelper;
        this.transferHelper = transferHelper;
        this.dateHelper = dateHelper;
        this.songMapper = songMapper;
        this.userMapper = userMapper;
        this.youTubeConfig = youTubeConfig;
        this.stationPlayerHelper = stationPlayerHelper;
        this.historyChannel = historyChannel;
        this.stationSongSkipHelper = stationSongSkipHelper;
        this.stationService = stationService;
        this.shiftSongChannel = shiftSongChannel;
        this.stationOnlineService = stationOnlineService;
    }

    @Override
    public Flux<SongDTO> getListSongByStationId(String stationId) {
        return stationService.retrieveByIdOrFriendlyId(stationId).flatMapMany(station -> {
            List<String> listSongId = station.getPlaylist();
            return getListSongByListSongId(listSongId);
        }).switchIfEmpty(Mono.error(new RadioBadRequestException("Invalid station ID!")));
    }

    public Flux<SongDTO> getListSongByListSongId(List<String> listSongId) {
        return songRepository.findByIdIn(listSongId).handle((song, sink) -> {
            SongDTO result = songMapper.songToSongDTO(song);
            if (song.getCreatorId() != null) {
                Optional<User> creator = userRepository.findById(song.getCreatorId());
                if (creator.isPresent()) {
                    result.setCreator(userMapper.userToUserDTO(creator.get()));
                }

                if (!song.getUpVoteUserIdList().isEmpty()) {
                    result.setUpvoteUserList(userRepository.findByIdIn(song.getUpVoteUserIdList())
                            .stream()
                            .map(userMapper::userToUserDTO)
                            .collect(Collectors.toList()));
                }

                if (!song.getDownVoteUserIdList().isEmpty()) {
                    result.setDownvoteUserList(userRepository.findByIdIn(song.getDownVoteUserIdList())
                            .stream()
                            .map(userMapper::userToUserDTO)
                            .collect(Collectors.toList()));
                }

                sink.next(result);
            }
        });
    }

    @Override
    public boolean existsBySongId(String songId) {
        return songRepository.findFirstBySongId(songId)
            .blockOptional().isPresent();
    }

    @Override
    public Flux<SongDTO> getListSongByListSongIdId(List<String> listSongId) {
        return songRepository.findBySongIdIn(listSongId).map(songMapper::songToSongDTO);
    }

    @Override
    public Flux<SongDTO> getAllSongById(List<String> idList) {
        return songRepository.findAllById(idList)
                .map(songMapper::songToSongDTO).defaultIfEmpty(new SongDTO());
    }

    @Override
    public Mono<PlayList> getPlayListByStationId(String stationId, long joinTime) {
        return stationService.retrieveByIdOrFriendlyId(stationId)
            .map(station -> {
                List<String> listSongId = station.getPlaylist();
                return getListSongByListSongId(listSongId);
            })
            .flatMap(songDTOFlux -> songDTOFlux
                .collectList()
                .map(listSong -> createPlayListFromListSong(listSong, stationId, joinTime)))
            .onErrorResume(Exception.class, ex -> {
                stationPlayerHelper.clearNowPlayingByStationId(stationId);
                return Mono.just(PlayList.EMPTY_PLAYLIST);
            });
    }

    private PlayList createPlayListFromListSong(List<SongDTO> listSong, String stationId, long joinTime) {
        PlayList playList = new PlayList();
        Optional<NowPlaying> nowPlaying = stationPlayerHelper.getStationNowPlaying(stationId);
        Optional<NowPlaying> previousPlay = stationPlayerHelper.getPreviousPlay(stationId);
        final String previousSongId = previousPlay.isPresent() ? previousPlay.get().getSongId() : BLANK;
        listSong = listSong.stream()
                           .filter(songDTO -> songDTO.getStatus() != SongStatus.played
                                   && songDTO.getStatus() != SongStatus.skipped
                                   && (songDTO.getStatus() != SongStatus.playing || !songDTO.getId().equals(previousSongId)))
                           .sorted(sortByVoteAndStatus)
                           .collect(Collectors.toList());
        Optional<SongDTO> nowPlayingSong = listSong.stream()
                                                   .filter(songDTO -> songDTO.getStatus() == SongStatus.playing)
                                                   .findFirst();

        if (joinTime != 0){
            nowPlaying = seekTime(listSong, nowPlaying, joinTime, stationId);
        }

        if (!listSong.isEmpty()) {
            if (nowPlaying.isPresent()) {
                nowPlaying = handleNowPlaying(nowPlayingSong, nowPlaying, stationId, listSong, joinTime);
            } else {
                nowPlaying = updateStatusAndSetNowPlayingFromSong(nowPlayingSong.isPresent() ? nowPlayingSong.get() : listSong.get(0), stationId, joinTime);
            }
        } else {
            stationPlayerHelper.clearNowPlayingByStationId(stationId);
            nowPlaying = Optional.empty();
        }

        playList.setListSong(listSong);
        playList.setNowPlaying(nowPlaying.isPresent() ? nowPlaying.get() : PlayList.EMPTY_PLAYLIST.getNowPlaying());

        return playList;
    }

    private Optional<NowPlaying> seekTime(List<SongDTO> listSong, Optional<NowPlaying> nowPlaying, long joinTime, String stationId) {
        if (nowPlaying.isPresent()) {
            long lastPlayingTime = nowPlaying.get().getStartingTime();
            long differentTime = joinTime - lastPlayingTime;
            List<SongDTO> shiftSongList = new ArrayList<>();
            SongDTO willBePlaySong = null;
            for (SongDTO currentSong : listSong) {
                long durationInSecond = currentSong.getDuration() / 1000;
                if (durationInSecond < differentTime) {
                    differentTime -= durationInSecond;
                    shiftSongList.add(currentSong);
                    willBePlaySong = currentSong;
                } else {
                    willBePlaySong = currentSong;
                    break;
                }
            }

            if (shiftSongList.isEmpty()) {
                nowPlaying = stationPlayerHelper.addNowPlaying(stationId, willBePlaySong, lastPlayingTime);
            } else {
                nowPlaying = stationPlayerHelper.addNowPlaying(stationId, willBePlaySong, joinTime);
                updateShiftSong(shiftSongList);
            }
        }

        return nowPlaying;
    }

    private void updateShiftSong(List<SongDTO> listShiftSong) {
        Map<String, Object> shiftSongParam = new HashMap<>();
        shiftSongParam.put(EventDataKeys.event_id.name(), SubscriptionEvents.shift_song.name());
        shiftSongParam.put(EventDataKeys.list_shift_song.name(), listShiftSong);

        shiftSongChannel.send(MessageBuilder.withPayload(shiftSongParam).build());

    }

    private Comparator<SongDTO> sortByVoteAndStatus = (SongDTO song1, SongDTO song2) -> {
        if (song1.getStatus() == SongStatus.playing){
            return -1;
        }

        if (song2.getStatus() == SongStatus.playing){
            return 1;
        }

        int song1Vote = song1.getUpVoteCount() - song1.getDownVoteCount();
        int song2Vote = song2.getUpVoteCount() - song2.getDownVoteCount();

        if (song1Vote > song2Vote){
            return -1;
        } else if (song1Vote < song2Vote){
            return 1;
        } else{
            return 0;
        }
    };

    private Optional<NowPlaying> handleNowPlaying(Optional<SongDTO> nowPlayingSong, Optional<NowPlaying> nowPlaying, String stationId, List<SongDTO> listSong, long jointTime) {
        final String songId = nowPlaying.get().getSongId();
        if (nowPlayingSong.isPresent() && !songId.equals(nowPlayingSong.get().getId())){
            List<SongDTO> listSongClone = new ArrayList<>(listSong);
            for (SongDTO currentSong : listSongClone){
                if (!currentSong.getId().equals(songId)){
                    listSong.remove(currentSong);
                } else {
                    break;
                }
            }
        }
        if (nowPlaying.get().isEnded()) {
            String endedSongId = nowPlaying.get().getSongId();
            nowPlaying = getNextSongFromList(stationId, endedSongId, listSong, jointTime);
        } else {
            Optional<Set<SongDTO>> stationSkipSong = stationSongSkipHelper.getListSkipSong(stationId);
            Set<String> listSkippedSongId = Collections.EMPTY_SET;
            if (stationSkipSong.isPresent()) {
                listSkippedSongId = stationSkipSong.get().stream().map(SongDTO::getId).collect(Collectors.toSet());
            }
            if (!listSkippedSongId.isEmpty()) {
                if (listSkippedSongId.contains(nowPlaying.get().getSongId())) {
                    stationSongSkipHelper.removeSkipSong(stationId, nowPlayingSong.get());
                    nowPlaying.get().setSkipped(true);
                        try {
                            Thread.sleep(TOTAL_TIME_SKIP*1000 + 2100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finally {
                            String skippedSongId = nowPlaying.get().getSongId();
                            nowPlaying = skipSongAndRemoveFromListBySongId(stationId, skippedSongId, listSong, jointTime);
                        }
                }
                changeFlagForWillBeSkipSongInList(listSong, listSkippedSongId);
            }
            changeFlagForNotSkipSongAnyMore(listSong, listSkippedSongId);
        }
        return nowPlaying;
    }

    private void moveToHistory(String stationId, String songId) {
        Map<String, Object> historyParam = new HashMap<>();
        historyParam.put(EventDataKeys.event_id.name(), SubscriptionEvents.song_history.name());
        historyParam.put(EventDataKeys.stationId.name(), stationId);
        historyParam.put(EventDataKeys.songId.name(), songId);

        historyChannel.send(MessageBuilder.withPayload(historyParam).build());
    }

    private void changeFlagForWillBeSkipSongInList(List<SongDTO> listSong, Set<String> listSkipSongId){
        listSong.forEach(songDTO -> {
            if (listSkipSongId.contains(songDTO.getId())) {
                songDTO.setSkipped(true);
            }
        });
    }

    private void changeFlagForNotSkipSongAnyMore(List<SongDTO> listSong, Set<String> notChangeSongId) {
        Set<String> updateList = listSong.stream()
                .filter(getSongNotSkipAnyMore(notChangeSongId))
                .map(SongDTO::getId)
                .collect(Collectors.toSet());
        if (!updateList.isEmpty()) {
            listSong.forEach(currentSong -> {
                if (updateList.contains(currentSong.getId())){
                    currentSong.setSkipped(false);
                }
            });
        }
    }

    private Predicate<SongDTO> getSongNotSkipAnyMore(Set<String> notChangeSongId) {
        if (!notChangeSongId.isEmpty()) {
            return songDTO -> !notChangeSongId.contains(songDTO.getId())
                            && songDTO.getMessage() != null
                            && songDTO.isSkipped();
        } else {
            return songDTO -> songDTO.getMessage() != null
                           && songDTO.isSkipped();
        }
    }

    private Optional<NowPlaying> skipSongAndRemoveFromListBySongId(String stationId, String skipSongId, List<SongDTO> listSong, long joinTime){
        SongDTO skippedSong = listSong.stream().filter(songDTO -> songDTO.getId().equals(skipSongId)).findFirst().get();
        listSong.remove(skippedSong);
        updateSongPlayingStatusAndMessage(skipSongId, SongStatus.skipped, skippedSong.getMessage());
        moveToHistory(stationId, skipSongId);

        SongDTO nowPlayingSong = listSong.get(0);
        return updateStatusAndSetNowPlayingFromSong(nowPlayingSong, stationId, joinTime);
    }

    private Optional<NowPlaying> getNextSongFromList(String stationId, String currentPlaySongId, List<SongDTO> listSong, long joinTime){
        Optional<SongDTO> removeSong = listSong.stream().filter(songDTO -> songDTO.getId().equals(currentPlaySongId)).findFirst();
        if (removeSong.isPresent()) {
            listSong.remove(removeSong.get());
            updateSongPlayingStatusAndMessage(currentPlaySongId, SongStatus.played, removeSong.get().getMessage());
            moveToHistory(stationId, currentPlaySongId);
        }

        SongDTO nowPlayingSong = listSong.get(0);
        return updateStatusAndSetNowPlayingFromSong(nowPlayingSong, stationId, joinTime);
    }

    private Optional<NowPlaying> updateStatusAndSetNowPlayingFromSong(SongDTO nowPlayingSong, String stationId, long joinTime) {
        updateSongPlayingStatusAndMessage(nowPlayingSong.getId(), SongStatus.playing, nowPlayingSong.getMessage());
        return stationPlayerHelper.addNowPlaying(stationId, nowPlayingSong, joinTime);
    }

    private void updateSongPlayingStatusAndMessage(String songId, SongStatus playingStatus, String message){
        songRepository.findById(songId).flatMap(song -> {
            song.setStatus(playingStatus);
            song.setMessage(message);
            return songRepository.save(song);
        }).subscribe();
    }

    private boolean checkAddSongPermission(String stationId, User user) {
        Optional<Station> stationOptional = stationService.retrieveByIdOrFriendlyId(stationId).blockOptional();

        if (!stationOptional.isPresent()) {
            throw new RadioNotFoundException();
        }
        if (!user.isAnonymous()) {
            return true;
        }

        Station station = stationOptional.get();
        if (user.isAnonymous() && user.getId().equals(station.getOwnerId())) {
            return true;
        }
        throw new RadioBadRequestException("Please login to use this feature!!!");
    }

    @Override
    public Mono<SongDTO> addSongToStationPlaylist(
            String stationId,
            String videoId,
            String message,
            User creator
    ) {

        checkAddSongPermission(stationId, creator);

        Song song = new Song();
        Video video = youTubeHelper.getYouTubeVideoById(videoId);

        if(video.getId() == null){
            return Mono.error(new SongNotFoundException());
        } else {
            song.setSongId(video.getId());
            song.setTitle(video.getSnippet().getTitle());
            song.setCreatedAt(dateHelper.convertDateToLocalDate(new Date()));
            song.setMessage(message);
            song.setThumbnail(video.getSnippet().getThumbnails().getDefault().getUrl());
            song.setUrl(youTubeConfig.getUrl() + videoId + "&t=0");
            song.setCreatorId(creator.getId());
            song.setSource(video.getKind().split("#")[0]);
            song.setDuration(transferHelper.transferVideoDuration(video.getContentDetails().getDuration()));
            song.setStatus(SongStatus.not_play_yet);
        }

        return songRepository.save(song).flatMap(newSong ->
                stationService.retrieveByIdOrFriendlyId(stationId)
                        .switchIfEmpty(Mono.error(new RadioNotFoundException()))
                        .flatMap(station -> {
                            if (station.getPlaylist() == null) {
                                station.setPlaylist(new ArrayList<>());
                            }

                            station.getPlaylist().add(newSong.getId());

                            return stationRepository
                                    .save(station)
                                    .then(Mono.just(songMapper.songToSongDTO(newSong)));
                        })
        );
    }

    @Override
    public Mono<SongDTO> upVoteSongInStationPlaylist(
            String stationId,
            String songId,
            String userId
    ) {
        return findSong(stationId, songId).flatMap(song -> {
            if (song.getCreatorId().equals(userId)) {
                return Mono.error(new RadioBadRequestException("You can not upvote your own song."));
            }

            List<String> upVoteUserIdList = song.getUpVoteUserIdList();
            List<String> downVoteUserIdList = song.getDownVoteUserIdList();

            // Check if user is already upvote this station
            if (upVoteUserIdList.contains(userId)) {
                // if upvoted, remove userID record
                upVoteUserIdList.remove(userId);
            } else {
                // if NOT upvoted, add userID record
                downVoteUserIdList.remove(userId);
                upVoteUserIdList.add(userId);
            }

            return songRepository
                    .save(song)
                    .flatMap(songResult -> mapSongToSongDTO(songResult, stationId));
        });
    }

    @Override
    public Mono<SongDTO> downVoteSongInStationPlaylist(
            String stationId,
            String songId,
            String userId
    ) {
        return findSong(stationId, songId).flatMap(song -> {

            List<String> downVoteUserIdList = song.getDownVoteUserIdList();
            List<String> upVoteUserIdList = song.getUpVoteUserIdList();

            // Check if user is already downvote this station
            if (downVoteUserIdList.contains(userId)) {
                // if downvoted, remove userID record
                downVoteUserIdList.remove(userId);
            } else {
                // if NOT downvoted, add userID record
                upVoteUserIdList.remove(userId);
                downVoteUserIdList.add(userId);
            }

            return songRepository
                    .save(song)
                    .flatMap(songResult -> mapSongToSongDTO(songResult, stationId))
                    .flatMap(this::handleSkipRule);
        });
    }

    @Override
    public Mono<SongDTO> handleSkipRule (SongDTO songDTO){
        return Mono.just(stationService.getStationById(songDTO.getStationFriendlyId())).map(tempStation ->{
            final StationConfigurationDTO stationConfiguration = tempStation.getStationConfiguration();
            boolean isSkipped = false;

            if (stationConfiguration.getSkipRule().getSkipRuleType() == SkipRuleType.ADVANCE) {
                if (isOwnerDownvote(tempStation, songDTO)) {
                    isSkipped = true;
                }
            } else {
                double downVotePercent = calcCurrentSongDislikePercent(songDTO, tempStation);
                if (downVotePercent > DOWN_VOTE_THRES_PERCENT) {
                    isSkipped = true;
                }
            }
            if (isSkipped){
                stationSongSkipHelper.addSkipSong(songDTO.getStationFriendlyId(), songDTO);
            } else {
                stationSongSkipHelper.removeSkipSong(songDTO.getStationFriendlyId(), songDTO);
            }

            return songDTO;
        });
    }

    private double calcCurrentSongDislikePercent(SongDTO songDTO, StationDTO station) {
        final int numberOnline = stationOnlineService.getNumberOnlineUser(station.getFriendlyId());
        double currentSongDislikePercent = 0;
        if (numberOnline > 0) {
            currentSongDislikePercent = songDTO.getDownVoteCount() / (double) numberOnline;
        }
        return currentSongDislikePercent;
    }

    private boolean isOwnerDownvote(StationDTO station, SongDTO songDTO) {
        return songDTO.getDownvoteUserList().stream().anyMatch(userDTO -> userDTO.getId().equals(station.getOwnerId()));
    }

    /**
     * Find station by
     *
     * @param stationId id of station
     * @return station mono
     */
    private Mono<Station> findStation(String stationId) {
        return stationService
                .retrieveByIdOrFriendlyId(stationId);
    }

    /**
     * Check in station if station is existed
     *
     * @param stationId id of station
     * @param songId    id of station
     * @return station mono
     */
    private Mono<Song> findSong(String stationId, String songId) {
        return findStation(stationId)
            .flatMap(station -> {
                if (station.getPlaylist().contains(songId)) {
                    return songRepository
                            .findById(songId);
                }
            return Mono.error(new SongNotFoundException(songId));
        });
    }

    private Mono<SongDTO> mapSongToSongDTO(Song song, String stationFriendly) {
        SongDTO songDTO = songMapper.songToSongDTO(song);

        List<User> upVoteUserList = userRepository.findByIdIn(song.getUpVoteUserIdList());
        for (User user : upVoteUserList) {
            UserDTO userDTO = userMapper.userToUserDTO(user);
            songDTO.getUpvoteUserList().add(userDTO);
        }

        List<User> downVoteUserList = userRepository.findByIdIn(song.getDownVoteUserIdList());
        for (User user : downVoteUserList) {
            UserDTO userDTO = userMapper.userToUserDTO(user);
            songDTO.getDownvoteUserList().add(userDTO);
        }

        songDTO.setStationFriendlyId(stationFriendly);

        return Mono.just(songDTO);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return songRepository.existsById(id);
    }

    @Override
    public Mono<SongDTO> getById(String id) {
        return songRepository.findById(id)
                    .map(songMapper::songToSongDTO)
                    .switchIfEmpty(Mono.error(new RadioNotFoundException()));
    }


    @Override
    public Mono<SongDTO> updateSongSkippedStatusToDb(Mono<SongDTO> songDTOMono) {
        return songDTOMono.map(songDTO -> {
            songRepository.findById(songDTO.getId()).flatMap(song -> {
                song.setSkipped(true);
                return songRepository.save(song);
            }).doOnSuccess(song -> songDTO.setSkipped(song.isSkipped())).subscribe();

            return songDTO;
        });
    }
}
