package com.mgmtp.radio.mapper.chat;

import com.mgmtp.radio.domain.chat.User;
import com.mgmtp.radio.dto.chat.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);

    User userDtoToUser(UserDTO userDTO);
}
