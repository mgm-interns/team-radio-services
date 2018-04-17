package com.mgmtp.radio.respository.station;

import com.mgmtp.radio.domain.station.Song;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public interface SongRepository extends ReactiveMongoRepository<Song, String> {
    Flux<Song> findByIdIn(List<String> songId);
    Flux<Song> findBySongIdIn(List<String> songId);
    Mono<Song> findFirstBySongId(String songId);
}
