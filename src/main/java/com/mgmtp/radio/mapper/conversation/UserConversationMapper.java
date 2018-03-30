package com.mgmtp.radio.mapper.conversation;

import com.mgmtp.radio.domain.conversation.User;
import com.mgmtp.radio.dto.conversation.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConversationMapper {

    UserConversationMapper INSTANCE = Mappers.getMapper(UserConversationMapper.class);

    UserDTO userToUserDTO(User user);

    User userDtoToUser(UserDTO userDTO);
}
