package com.mgmtp.radio.service.user;

import com.mgmtp.radio.domain.user.Role;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.social.facebook.model.FacebookAvatar;
import com.mgmtp.radio.social.facebook.model.FacebookUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
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


    @Override
    public User registerByFacebook(FacebookUser facebookUser, FacebookAvatar facebookAvatar) {

        Optional<User> existUser = Optional.empty();
        if (Optional.of(facebookUser).isPresent()) {
            existUser = userRepository.findByEmail(facebookUser.email);
        }

        if (existUser.isPresent()) {
            User user = existUser.get();

            user.setFacebookId(facebookUser.id);

            String name = StringUtils.isEmpty(user.getName()) ? facebookUser.name : user.getName();
            user.setName(name);

            String firstName = StringUtils.isEmpty(user.getFirstName()) ? facebookUser.firstName : user.getName();
            user.setFirstName(firstName);

            String lastName = StringUtils.isEmpty(user.getLastName()) ? facebookUser.lastName : user.getLastName();
            user.setLastName(lastName);

            String userAvatarUrl = StringUtils.isEmpty(user.getAvatarUrl()) ? facebookAvatar.data.url : user.getAvatarUrl();
            user.setAvatarUrl(userAvatarUrl);

            return userRepository.save(existUser.get());

        } else {

            User user = new User();
            user.setFacebookId(facebookUser.id);
            user.setPassword(UUID.randomUUID().toString());
            user.setName(facebookUser.name);
            user.setFirstName(facebookUser.firstName);
            user.setLastName(facebookUser.lastName);
            user.setEmail(facebookUser.email);
            String userAvatarUrl = StringUtils.isEmpty(user.getAvatarUrl()) ? facebookAvatar.data.url : user.getAvatarUrl();
            user.setAvatarUrl(userAvatarUrl);
            return user;
        }
    }

    @Override
    public Optional<User> getUserByFacebookId(String facebookId) {
        return userRepository.findFirstByFacebookId(facebookId);
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
