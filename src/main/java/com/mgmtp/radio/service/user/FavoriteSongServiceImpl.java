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
		Flux<FavoriteSongDTO> favoriteSongFlux = favoriteSongRepository.findByUserId(userId).map(favoriteSongMapper::favoriteSongToFavoriteSongDTO);

		Mono<Map<String, FavoriteSongDTO>> favoriteSongDTOMapMono = favoriteSongFlux.collectMap(
			FavoriteSongDTO::getSongId,
			favoriteSongDTO -> favoriteSongDTO
		);

		Flux<FavoriteSongDTO> result = favoriteSongDTOMapMono.map(favoriteSongDTOList -> {
			List<String> listSongId = new ArrayList<>(favoriteSongDTOList.keySet());
			songService.getListSongByListSongId(listSongId).map(songDTO -> {
				favoriteSongDTOList.get(songDTO.getId()).setSong(songDTO);
				return songDTO;
			}).subscribe();
			return favoriteSongDTOList;
		}).flatMapMany(map ->
			Flux.fromIterable(new ArrayList<>(map.values()))
		);

		return result;
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
