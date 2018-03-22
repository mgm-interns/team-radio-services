package com.mgmtp.radio.mapper.station;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.dto.station.SongDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SongMapper {
	SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);

	@Mapping(target = "upVoteUserList", ignore = true)
	@Mapping(target = "downVoteUserList", ignore = true)
	SongDTO songToSongDTO(Song song);

	@Mapping(target = "upVoteUserIdList", ignore = true)
	@Mapping(target = "downVoteUserIdList", ignore = true)
	Song songDtoToSong(SongDTO songDTO);
}
