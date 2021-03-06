package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.FavoriteSong;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteSongRepository extends ReactiveMongoRepository<FavoriteSong, String> {
	Flux<FavoriteSong> findByUserId(String id);

	Mono<FavoriteSong> findByUserIdAndSongId(String userId, String songId);

	Mono<FavoriteSong> findBySongIdAndUserId(String id, String userId);

	Mono<Long> deleteBySongIdAndUserId(String songId, String userId);
}
