package com.mgmtp.radio.service.user;

import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteSongService {

	Mono<FavoriteSongDTO> create(String userId, FavoriteSongDTO favoriteSongDTO);

	Flux<FavoriteSongDTO> findByUserId(String userId);

	Mono<Boolean> existsByUserIdAndSongId(String userId, String songId);

	Mono<FavoriteSongDTO> delete(String id, String userId);
}
