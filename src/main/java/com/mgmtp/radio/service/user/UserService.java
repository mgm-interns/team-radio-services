package com.mgmtp.radio.service.user;

import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    UserDTO getUserByUsername(String username) throws RadioNotFoundException;
    UserDTO register(UserDTO userDTO);
    UserDTO patchUser(String userId, UserDTO userDTO) throws RadioNotFoundException;
    UserDTO patchUserAvatar(String userId, String avatarUrl) throws RadioNotFoundException;
    UserDTO patchUserCover(String userId, String coverUrl) throws RadioNotFoundException;
    UserDTO getUserById(String id) throws RadioNotFoundException;
    List<UserDTO> findUserByUpdatedAt(LocalDate date);
}
