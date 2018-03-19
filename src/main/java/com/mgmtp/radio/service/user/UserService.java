package com.mgmtp.radio.service.user;

import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;

public interface UserService {
    UserDTO getUserByUsername(String username) throws RadioNotFoundException;
    UserDTO register(UserDTO userDTO);
    UserDTO patchUser(String username, UserDTO userDTO) throws RadioNotFoundException;
}
