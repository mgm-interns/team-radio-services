package com.mgmtp.radio.service.user;

import com.mgmtp.radio.domain.user.FavoriteSong;
import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.FavoriteSongMapper;
import com.mgmtp.radio.respository.user.FavoriteSongRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class FavoriteSongServiceImpl implements FavoriteSongService {

	private final FavoriteSongRepository favoriteSongRepository;
	private final FavoriteSongMapper favoriteSongMapper;

	public FavoriteSongServiceImpl(FavoriteSongRepository favoriteSongRepository, FavoriteSongMapper favoriteSongMapper) {
		this.favoriteSongRepository = favoriteSongRepository;
		this.favoriteSongMapper = favoriteSongMapper;
	}

	@Override
	public Mono<FavoriteSongDTO> create(String userId, FavoriteSongDTO favoriteSongDTO) {
		favoriteSongDTO.setUserId(userId);
		favoriteSongDTO.setCreatedAt(LocalDate.now());
		FavoriteSong favoriteSong = favoriteSongMapper.favoriteSongDtoToFavoriteSong(favoriteSongDTO);
		return favoriteSongRepository.save(favoriteSong).map(song -> favoriteSongMapper.favoriteSongToFavoriteSongDTO(song));
	}

	@Override
	public Flux<FavoriteSongDTO> findByUserId(String userId) throws RadioNotFoundException {
		return favoriteSongRepository.findByUserId(userId).map(favoriteSongMapper::favoriteSongToFavoriteSongDTO).switchIfEmpty(Mono.error(new RadioNotFoundException()));
	}

	@Override
	public Mono<FavoriteSongDTO> findByUserIdAndSongId(String userId, String songId) throws RadioNotFoundException {
		return favoriteSongRepository.findByUserIdAndSongId(userId, songId).map(favoriteSongMapper::favoriteSongToFavoriteSongDTO).switchIfEmpty(Mono.error(new RadioNotFoundException()));
	}
}
