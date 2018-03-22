package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.SongNotFoundException;
import com.mgmtp.radio.exception.StationNotFoundException;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("songService")
public class SongServiceImpl implements SongService {
    private final StationRepository stationRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SongMapper songMapper;
    private final UserMapper userMapper;

    public SongServiceImpl(StationRepository stationRepository, SongRepository songRepository, UserRepository userRepository,
                           SongMapper songMapper, UserMapper userMapper) {
        this.stationRepository = stationRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.songMapper = songMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Flux<SongDTO> getListSongByStationId(String stationId) {
        return stationRepository.findByIdAndDeletedFalse(stationId).flatMapMany(station -> {
            List<String> listSongId = station.getPlaylist();
            return getListSongByListSongId(listSongId);
        }).switchIfEmpty(Mono.error(new RadioBadRequestException("Invalid station ID!")));
    }

    private Flux<SongDTO> getListSongByListSongId(List<String> listSongId) {
        return songRepository.findByIdIn(listSongId).map(song -> {
            SongDTO result = songMapper.songToSongDTO(song);
            Optional<User> creator = userRepository.findById(song.getCreatorId());
            result.setCreator(creator.isPresent() ? userMapper.userToUserDTO(creator.get()) : null);
            return result;
        }).switchIfEmpty(Mono.error(new RadioNotFoundException("Not found song in station")));
    }

    @Override
    public Flux<SongDTO> getAllSongById(List<String> idList) {
        return songRepository.findAllById(idList)
                .map(songMapper::songToSongDTO).defaultIfEmpty(new SongDTO());
    }


    @Override
    public Mono<SongDTO> addSongToStationPlaylist(
            String stationId,
            SongDTO songDTO
    ) {

        Song song = songMapper.songDtoToSong(songDTO);

        return songRepository.save(song).flatMap(newSong ->
                findStation(stationId).flatMap(station -> {

                    // Update station playlist
                    station.getPlaylist().add(newSong.getId());

                    return stationRepository
                            .save(station)
                            .then(mapSongToSongDTO(newSong));
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

            List<String> userIdList = song.getUpVoteUserIdList();
            // Check if user is already upvote this station
            if (userIdList.contains(userId)) {
                // if upvoted, remove userID record
                userIdList.remove(userId);
            } else {
                // if NOT upvoted, add userID record
                userIdList.add(userId);
            }
            return songRepository
                    .save(song)
                    .flatMap(this::mapSongToSongDTO);
        });
    }

    @Override
    public Mono<SongDTO> downVoteSongInStationPlaylist(
            String stationId,
            String songId,
            String userId
    ) {
        return findSong(stationId, songId).flatMap(song -> {
            if (song.getCreatorId().equals(userId)) {
                return Mono.error(new RadioBadRequestException("You can not downvote your own song."));
            }

            List<String> userIdList = song.getDownVoteUserIdList();
            // Check if user is already downvote this station
            if (userIdList.contains(userId)) {
                // if downvoted, remove userID record
                userIdList.remove(userId);
            } else {
                // if NOT downvoted, add userID record
                userIdList.add(userId);
            }
            return songRepository
                    .save(song)
                    .flatMap(this::mapSongToSongDTO);
        });
    }

    /**
     * Find station by
     *
     * @param stationId id of station
     * @return station mono
     */
    private Mono<Station> findStation(String stationId) {
        return stationRepository
                .findById(stationId)
                .switchIfEmpty(Mono.error(new StationNotFoundException(stationId)));
    }

    /**
     * Check in station if station is existed
     *
     * @param stationId id of station
     * @param songId    id of station
     * @return station mono
     */
    private Mono<Song> findSong(String stationId, String songId) {
        return findStation(stationId).flatMap(station -> {
            if (station.getPlaylist().contains(songId)) {
                return songRepository
                        .findById(songId);
            }
            return Mono.error(new SongNotFoundException(songId));
        });
    }

    private Mono<SongDTO> mapSongToSongDTO(Song song) {
        SongDTO songDTO = songMapper.songToSongDTO(song);

        songDTO.setUpvoteUserList(new ArrayList<>());
        List<User> upVoteUserList = userRepository.findByIdIn(song.getUpVoteUserIdList());
        for (User user : upVoteUserList) {
            UserDTO userDTO = userMapper.userToUserDTO(user);
            songDTO.getUpvoteUserList().add(userDTO);
        }

        songDTO.setDownvoteUserList(new ArrayList<>());
        List<User> downVoteUserList = userRepository.findByIdIn(song.getDownVoteUserIdList());
        for (User user : downVoteUserList) {
            UserDTO userDTO = userMapper.userToUserDTO(user);
            songDTO.getDownvoteUserList().add(userDTO);
        }

        return Mono.just(songDTO);
    }
}
