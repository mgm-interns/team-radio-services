package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<SongDTO> getListSongBy(String stationId) {
        return stationRepository.findByIdAndDeletedFalse(stationId).flatMapMany(station -> {
            List<String> playList = station.getPlaylist();

            return songRepository.findByIdIn(playList).map(song -> {
                SongDTO result = songMapper.songToSongDTO(song);
                Optional<User> creator = userRepository.findById(song.getCreatorId());
                result.setCreator(creator.isPresent() ? userMapper.userToUserDTO(creator.get()) : null);
                return result;
            }).switchIfEmpty(Mono.error(new RadioNotFoundException("Not found song in station")));
        }).switchIfEmpty(Mono.error(new RadioBadRequestException("Invalid station ID!")));
    }
}
