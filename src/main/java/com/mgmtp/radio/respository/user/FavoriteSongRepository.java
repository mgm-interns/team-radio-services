package com.mgmtp.radio.respository.user;

import com.mgmtp.radio.domain.user.FavoriteSong;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface FavoriteSongRepository extends ReactiveMongoRepository<FavoriteSong, String> {
	Flux<FavoriteSong> findByUserId(String id);
}
