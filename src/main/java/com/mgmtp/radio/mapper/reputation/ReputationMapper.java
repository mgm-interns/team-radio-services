package com.mgmtp.radio.mapper.reputation;

import com.mgmtp.radio.domain.reputation.Reputation;
import com.mgmtp.radio.dto.reputation.ReputationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReputationMapper {

    ReputationMapper INSTANCE = Mappers.getMapper(ReputationMapper.class);

    ReputationDTO reputationToReputationDTO(Reputation reputation);
}
