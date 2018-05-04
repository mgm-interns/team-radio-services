package com.mgmtp.radio.service.user;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.service.reputation.ReputationService;
import com.mgmtp.radio.support.CloudinaryHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    UserMapper userMapper = UserMapper.INSTANCE;

    UserService userService;

    ReputationService reputationService;

    PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(userMapper, userRepository, passwordEncoder,null, reputationService,null);
    }

    @Test
    public void getUserByUsername() throws Exception{
        final String USERNAME = "test_username";

        //given
        User user1 = new User();
        user1.setUsername(USERNAME);
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setName("John Doe");
        user1.setCountry("Germany");
        user1.setCity("Munich");
        user1.setEmail("john.doe@yopmail.com");

        //when
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.ofNullable(user1));
        UserDTO userDTO = userService.getUserByUsername(USERNAME);

        //then
        assertEquals(USERNAME, userDTO.getUsername());
        assertNotEquals(passwordEncoder.encode("password123"), userDTO.getPassword());
        assertEquals("John", userDTO.getFirstName());
        assertEquals("Doe", userDTO.getLastName());
        assertEquals("John Doe", userDTO.getName());
        assertEquals("Germany", userDTO.getCountry());
        assertEquals("Munich", userDTO.getCity());
        assertEquals("john.doe@yopmail.com", userDTO.getEmail());

    }

    @Test
    public void register() {
        //given
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("john.doe");
        userDTO.setPassword(passwordEncoder.encode("password123"));
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setName("John Doe");
        userDTO.setCountry("Germany");
        userDTO.setCity("Munich");
        userDTO.setEmail("john.doe@yopmail.com");

        User savedUser = new User();
        savedUser.setUsername(userDTO.getUsername());
        savedUser.setPassword(userDTO.getPassword());
        savedUser.setFirstName(userDTO.getFirstName());
        savedUser.setLastName(userDTO.getLastName());
        savedUser.setName(userDTO.getName());
        savedUser.setCountry(userDTO.getCountry());
        savedUser.setCity(userDTO.getCity());
        savedUser.setEmail(userDTO.getEmail());
        savedUser.setId("123456789");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        UserDTO savedUserDTO = userService.register(userDTO);

        //then
        assertEquals(userDTO.getUsername(), savedUserDTO.getUsername());
        assertNotEquals(userDTO.getPassword(), savedUserDTO.getPassword());
        assertEquals(userDTO.getName(), savedUserDTO.getName());
        assertEquals(userDTO.getFirstName(), savedUserDTO.getFirstName());
        assertEquals(userDTO.getLastName(), savedUserDTO.getLastName());
        assertEquals(userDTO.getCountry(), savedUserDTO.getCountry());
        assertEquals(userDTO.getCity(), savedUserDTO.getCity());
        assertEquals(userDTO.getEmail(), savedUserDTO.getEmail());
    }

    @Test
    public void patchUser() throws Exception {
        //given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Doe");
        userDTO.setName("John Doe");
        userDTO.setCountry("Germany");
        userDTO.setCity("Munich");
        userDTO.setEmail("john.doe@yopmail.com");

        User savedUser = new User();
        savedUser.setFirstName(userDTO.getFirstName());
        savedUser.setLastName(userDTO.getLastName());
        savedUser.setName(userDTO.getName());
        savedUser.setCountry(userDTO.getCountry());
        savedUser.setCity(userDTO.getCity());
        savedUser.setEmail(userDTO.getEmail());

        when(userRepository.findById(anyString())).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        UserDTO savedUserDTO = userService.patchUser("jane.doe", userDTO);

        //then
        assertEquals(userDTO.getFirstName(), savedUserDTO.getFirstName());
        assertEquals(userDTO.getLastName(), savedUserDTO.getLastName());
        assertEquals(userDTO.getName(), savedUserDTO.getName());
        assertEquals(userDTO.getFirstName(), savedUserDTO.getFirstName());
        assertEquals(userDTO.getLastName(), savedUserDTO.getLastName());
        assertEquals(userDTO.getCountry(), savedUserDTO.getCountry());
        assertEquals(userDTO.getCity(), savedUserDTO.getCity());
        assertEquals(userDTO.getEmail(), savedUserDTO.getEmail());
    }

    @Test
    public void patchUserAvatar() throws Exception {
        //given
        final String URL = "https://new_avatar_url";

        UserDTO userDTO = new UserDTO();
        userDTO.setAvatarUrl(URL);

        User savedUser = new User();
        savedUser.setAvatarUrl(URL);

        when(userRepository.findById(anyString())).thenReturn(Optional.ofNullable(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        UserDTO savedUserDTO = userService.patchUserAvatar("username", URL);

        //then
        assertEquals(URL, savedUserDTO.getAvatarUrl());
    }

    @Test
    public void patchUserCover() throws Exception {
        //given
        final String URL = "https://new_cover_url";

        UserDTO userDTO = new UserDTO();
        userDTO.setAvatarUrl(URL);

        User savedUser = new User();
        savedUser.setAvatarUrl(URL);

        when(userRepository.findById(anyString())).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        UserDTO savedUserDTO = userService.patchUserCover("username", URL);

        //then
        assertEquals(URL, savedUserDTO.getCoverUrl());
    }
}