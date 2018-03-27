package com.mgmtp.radio.service.user;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO getUserByUsername(String username) throws RadioNotFoundException {
        return userRepository.findByUsername(username)
                .map(userMapper::userToUserDTO)
                .orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO register(UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userMapper.userDtoToUser(userDTO);
        return userMapper.userToUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO patchUser(String username, UserDTO userDTO) throws RadioNotFoundException {
        return userRepository.findByUsername(username).map(user -> {
            user.setName(userDTO.getName());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setCountry(userDTO.getCountry());
            user.setCity(userDTO.getCity());
            user.setBio(userDTO.getBio());
            user.setAvatarUrl(userDTO.getAvatarUrl());
            user.setCoverUrl(userDTO.getCoverUrl());
            return userMapper.userToUserDTO(userRepository.save(user));
        }).orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO patchUserAvatar(String username, String avatarUrl) throws RadioNotFoundException {
        return userRepository.findByUsername(username).map(user -> {
            user.setAvatarUrl(avatarUrl);
            return userMapper.userToUserDTO(userRepository.save(user));
        }).orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO patchUserCover(String username, String coverUrl) throws RadioNotFoundException {
        return userRepository.findByUsername(username).map(user -> {
            user.setCoverUrl(coverUrl);
            return userMapper.userToUserDTO(userRepository.save(user));
        }).orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO getUserById(String id) throws RadioNotFoundException {
        return userRepository.findById(id).map(userMapper::userToUserDTO).orElseThrow(RadioNotFoundException::new);
    }
}
