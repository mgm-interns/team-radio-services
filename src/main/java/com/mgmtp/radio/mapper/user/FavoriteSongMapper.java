package com.mgmtp.radio.mapper.user;

import com.mgmtp.radio.domain.user.FavoriteSong;
import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FavoriteSongMapper {
	FavoriteSongMapper INSTANCE = Mappers.getMapper(FavoriteSongMapper.class);

	@Mapping(target = "song", ignore = true)
	FavoriteSongDTO favoriteSongToFavoriteSongDTO(FavoriteSong favoriteSong);

	FavoriteSong favoriteSongDtoToFavoriteSong(FavoriteSongDTO favoriteSongDTO);
}
