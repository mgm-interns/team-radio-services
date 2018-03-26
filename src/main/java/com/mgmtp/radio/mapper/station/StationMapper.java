package com.mgmtp.radio.mapper.station;

import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.mapper.decorator.SongMapperDecorator;
import com.mgmtp.radio.mapper.decorator.StationMapperDecorator;
import com.mgmtp.radio.respository.station.SongRepository;
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.service.station.SongServiceImpl;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Mapper
@DecoratedWith(StationMapperDecorator.class)
public interface StationMapper {
	StationMapper INSTANCE = Mappers.getMapper(StationMapper.class);
	@Mapping(target = "playlist", ignore = true)
	StationDTO stationToStationDTO(Station station);

	@Mapping(target = "playlist", ignore = true)
	Station stationDtoToStation(StationDTO stationDTO);
}
