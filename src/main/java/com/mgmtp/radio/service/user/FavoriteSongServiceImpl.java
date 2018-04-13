package com.mgmtp.radio.service.user;

import com.mgmtp.radio.domain.user.FavoriteSong;
import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.FavoriteSongMapper;
import com.mgmtp.radio.respository.user.FavoriteSongRepository;
import com.mgmtp.radio.service.station.SongService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteSongServiceImpl implements FavoriteSongService {

	private final FavoriteSongRepository favoriteSongRepository;
	private final FavoriteSongMapper favoriteSongMapper;
	private final SongService songService;

	public FavoriteSongServiceImpl(FavoriteSongRepository favoriteSongRepository, FavoriteSongMapper favoriteSongMapper, SongService songService) {
		this.favoriteSongRepository = favoriteSongRepository;
		this.favoriteSongMapper = favoriteSongMapper;
		this.songService = songService;
	}

	@Override
	public Mono<FavoriteSongDTO> create(String userId, FavoriteSongDTO favoriteSongDTO) {
		favoriteSongDTO.setUserId(userId);
		favoriteSongDTO.setCreatedAt(LocalDate.now());
		FavoriteSong favoriteSong = favoriteSongMapper.favoriteSongDtoToFavoriteSong(favoriteSongDTO);
		return favoriteSongRepository.save(favoriteSong).map(favoriteSongMapper::favoriteSongToFavoriteSongDTO);
	}

	@Override
	public Flux<FavoriteSongDTO> findByUserId(String userId) {
		Mono<Map<String, FavoriteSongDTO>> favoriteSongDTOMapMono = favoriteSongRepository.findByUserId(userId)
									.map(favoriteSongMapper::favoriteSongToFavoriteSongDTO)
									.collectMap(FavoriteSongDTO::getSongId, favoriteSongDTO -> favoriteSongDTO);

		Mono<List<FavoriteSongDTO>> result = favoriteSongDTOMapMono.map(favoriteSongDTOMap -> {
			List<String> listSongId = new ArrayList<>(favoriteSongDTOMap.keySet());
			songService.getListSongByListSongId(listSongId)
				.subscribe(songDTO -> favoriteSongDTOMap.get(songDTO.getId()).setSong(songDTO));
			List<FavoriteSongDTO> favoriteSongDTOList = new ArrayList<>(favoriteSongDTOMap.values());
			return favoriteSongDTOList;
		}).delayElement(Duration.ofMillis(100));

		return result.flatMapMany(Flux::fromIterable);
	}

	@Override
	public Mono<Boolean> existsByUserIdAndSongId(String userId, String songId) {
		return favoriteSongRepository.findByUserIdAndSongId(userId, songId).map(song -> true).switchIfEmpty(Mono.just(false));
	}

	@Override
	public Mono<FavoriteSongDTO> delete(String id, String userId) {
		return favoriteSongRepository.findByIdAndUserId(id, userId).flatMap(song -> {
			return favoriteSongRepository.delete(song).then(Mono.just(favoriteSongMapper.favoriteSongToFavoriteSongDTO(song)));
		}).switchIfEmpty(Mono.error(new RadioNotFoundException()));
	}
}
