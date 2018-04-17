package com.mgmtp.radio.service.station;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.HistoryDTO;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.station.HistoryMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.HistoryRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Service("historyService")
public class HistoryServiceImpl implements HistoryService{

    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public HistoryServiceImpl(HistoryRepository historyRepository, HistoryMapper historyMapper, UserRepository userRepository, UserMapper userMapper){
        this.historyRepository = historyRepository;
        this.historyMapper = historyMapper;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    @Override
    public Flux<HistoryDTO> getHistoryByStationId(String stationId) {
        return historyRepository.findByStationId(stationId)
            .map(history -> {
                HistoryDTO result =  historyMapper.historyToHistoryDto(history);
                Optional<User> creator = userRepository.findById(history.getCreatorId());
                result.setCreator(creator.isPresent() ? userMapper.userToUserDTO(creator.get()) : null);
                return result;
            })
            .filter(distinctUrl(HistoryDTO::getUrl))
            .switchIfEmpty(Mono.error(new RadioNotFoundException("No song in history!")));
    }

    private Predicate<HistoryDTO> distinctUrl(Function<HistoryDTO, String> getUrl) {
        Set<String> uniqueUrl = ConcurrentHashMap.newKeySet();
        return url -> uniqueUrl.add(getUrl.apply(url));
    }
}
