package com.mgmtp.radio.service.user;

import com.mgmtp.radio.domain.user.Role;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        user.setRoles(getDefaultRole());
        return userMapper.userToUserDTO(userRepository.save(user));
    }

    private Set<Role> getDefaultRole() {
        Role role = new Role();
        role.setAuthority("USER");
        return new HashSet<>(Arrays.asList(role));
    }

    @Override
    public UserDTO patchUser(String userId, UserDTO userDTO) throws RadioNotFoundException {
        return userRepository.findById(userId).map(user -> {
            user.setName(userDTO.getName());
            user.setUsername(userDTO.getUsername());
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
    public UserDTO patchUserAvatar(String userId, String avatarUrl) throws RadioNotFoundException {
        return userRepository.findById(userId).map(user -> {
            user.setAvatarUrl(avatarUrl);
            return userMapper.userToUserDTO(userRepository.save(user));
        }).orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO patchUserCover(String userId, String coverUrl) throws RadioNotFoundException {
        return userRepository.findById(userId).map(user -> {
            user.setCoverUrl(coverUrl);
            return userMapper.userToUserDTO(userRepository.save(user));
        }).orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO getUserById(String id) throws RadioNotFoundException {
        return userRepository.findById(id).map(userMapper::userToUserDTO).orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public List<UserDTO> findUserByUpdatedAt(LocalDate date) {
        return userRepository.findByUpdatedAtEquals(date).stream().map(userMapper::userToUserDTO).collect(Collectors.toList());
    }
}
