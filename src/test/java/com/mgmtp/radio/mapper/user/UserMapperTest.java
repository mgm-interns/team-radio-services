package com.mgmtp.radio.mapper.user;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserMapperTest {

    UserMapper userMapper = UserMapper.INSTANCE;

    private final static String USERNAME = "john.doe";
    private final static String PASSWORD = "password";
    private final static String EMAIL = "john.doe@yopmail.com";
    private final static String NAME = "John Doe";

    @Test
    public void userToUserDTO() throws Exception {

        //given
        User user = new User();
        user.setPassword(PASSWORD);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setName(NAME);

        //when
        UserDTO userDTO = userMapper.userToUserDTO(user);

        //then
        assertNotEquals(user.getPassword(), userDTO.getPassword());
        assertEquals(user.getUsername(), userDTO.getUsername());
        assertEquals(user.getName(), userDTO.getName());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getId(), userDTO.getId());
    }

    @Test
    public void userDtoToUser() throws Exception {

        //given
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword(PASSWORD);
        userDTO.setUsername(USERNAME);
        userDTO.setEmail(EMAIL);
        userDTO.setName(NAME);

        //when
        User user = userMapper.userDtoToUser(userDTO);

        //then
        assertEquals(userDTO.getPassword(), user.getPassword());
        assertEquals(userDTO.getUsername(), user.getUsername());
        assertEquals(userDTO.getName(), user.getName());
        assertEquals(userDTO.getEmail(), user.getEmail());
        assertEquals(userDTO.getId(), user.getId());
    }
}