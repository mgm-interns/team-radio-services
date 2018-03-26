package com.mgmtp.radio.mapper.station;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.mapper.decorator.SongMapperDecorator;
import com.mgmtp.radio.mapper.decorator.StationMapperDecorator;
import com.mgmtp.radio.service.station.SongService;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
@DecoratedWith(SongMapperDecorator.class)
public interface SongMapper {
	SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);

	@Mapping(target = "upVoteCount", ignore = true)
	@Mapping(target = "downVoteCount", ignore = true)
	SongDTO songToSongDTO(Song song);

	@Mapping(target = "upVoteUserIdList", ignore = true)
	@Mapping(target = "downVoteUserIdList", ignore = true)
	Song songDtoToSong(SongDTO songDTO);
}
