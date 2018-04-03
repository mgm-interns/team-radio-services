package com.mgmtp.radio.mapper.conversation;

import com.mgmtp.radio.domain.conversation.FromUser;
import com.mgmtp.radio.dto.conversation.FromUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FromUserMapper {

    FromUserMapper INSTANCE = Mappers.getMapper(FromUserMapper.class);

    FromUserDTO fromUserToFromUserDTO(FromUser user);

    FromUser fromUserDtoToFromUser(FromUserDTO fromUserDTO);
}
