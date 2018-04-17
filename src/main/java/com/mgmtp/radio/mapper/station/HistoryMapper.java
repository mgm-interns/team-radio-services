package com.mgmtp.radio.mapper.station;

import com.mgmtp.radio.domain.station.History;
import com.mgmtp.radio.dto.station.HistoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HistoryMapper {
    HistoryMapper INSTANCE = Mappers.getMapper(HistoryMapper.class);

    HistoryDTO historyToHistoryDto(History history);

    History historyDtoToHistory(HistoryDTO historyDTO);
}
