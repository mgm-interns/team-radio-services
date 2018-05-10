package com.mgmtp.radio.service.user;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.domain.user.Role;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.station.StationMapper;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.reputation.ReputationService;
import com.mgmtp.radio.social.facebook.model.FacebookAvatar;
import com.mgmtp.radio.social.facebook.model.FacebookUser;
import com.mgmtp.radio.social.google.model.GoogleUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.springframework.util.StringUtils;

import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;
    private final ReputationService reputationService;
    private final Constant constant;

    public UserServiceImpl(UserMapper userMapper,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           StationRepository stationRepository,
                           ReputationService reputationService,
                           StationMapper stationMapper, Constant constant) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.stationRepository= stationRepository;
        this.stationMapper = stationMapper;
        this.reputationService = reputationService;
        this.constant = constant;
    }

    @Override
    public UserDTO getUserByUsername(String username) throws RadioNotFoundException {
        return userRepository.findByUsername(username)
                .map(userMapper::userToUserDTO)
                .orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO getUserByEmail(String email) throws RadioNotFoundException {
        return userRepository.findFirstByEmail(email)
                .map(userMapper::userToUserDTO)
                .orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO register(UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userMapper.userDtoToUser(userDTO);
        user.setRoles(getDefaultRole());
        User savedUser = userRepository.save(user);
        savedUser = reputationService.updateUserReputation(savedUser);
        return userMapper.userToUserDTO(savedUser);
    }


    @Override
    public User registerByFacebook(FacebookUser facebookUser, FacebookAvatar facebookAvatar) {

        Optional<User> existUser = Optional.empty();

        if (Optional.ofNullable(facebookUser).isPresent() && facebookUser.email != null) {
            existUser = userRepository.findFirstByEmail(facebookUser.email);
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

            User savedUser = userRepository.save(existUser.get());
            savedUser = reputationService.updateUserReputation(savedUser);
            return savedUser;

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
            User savedUser = userRepository.save(user);
            savedUser = reputationService.updateUserReputation(savedUser);
            return savedUser;
        }
    }

    @Override
    public Optional<User> getUserByFacebookId(String facebookId) {
        return userRepository.findFirstByFacebookId(facebookId);
    }

    @Override
    public User registerByGoogle(GoogleUser googleUser) {
        Optional<User> existUser = Optional.empty();
        if (Optional.ofNullable(googleUser).isPresent()) {
            existUser = userRepository.findFirstByEmail(googleUser.email);
        }

        if (existUser.isPresent()) {
            User user = existUser.get();

            user.setGoogleId(googleUser.id);

            String name = StringUtils.isEmpty(user.getName()) ? googleUser.name : user.getName();
            user.setName(name);

            String firstName = StringUtils.isEmpty(user.getFirstName()) ? googleUser.firstName : user.getName();
            user.setFirstName(firstName);

            String lastName = StringUtils.isEmpty(user.getLastName()) ? googleUser.lastName : user.getLastName();
            user.setLastName(lastName);

            String userAvatarUrl = StringUtils.isEmpty(user.getAvatarUrl()) ? googleUser.picture : user.getAvatarUrl();
            user.setAvatarUrl(userAvatarUrl);

            User savedUser = userRepository.save(existUser.get());
            savedUser = reputationService.updateUserReputation(savedUser);
            return savedUser;

        } else {

            User user = new User();
            user.setFacebookId(googleUser.id);
            user.setPassword(UUID.randomUUID().toString());
            user.setName(googleUser.name);
            user.setFirstName(googleUser.firstName);
            user.setLastName(googleUser.lastName);
            user.setEmail(googleUser.email);
            String userAvatarUrl = StringUtils.isEmpty(user.getAvatarUrl()) ? googleUser.picture : user.getAvatarUrl();
            user.setAvatarUrl(userAvatarUrl);
            User savedUser = userRepository.save(user);
            savedUser = reputationService.updateUserReputation(savedUser);
            return savedUser;
        }
    }

    @Override
    public Optional<User> getUserByGoogleId(String googleId) {
        return userRepository.findFirstByGoogleId(googleId);
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
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setCountry(userDTO.getCountry());
            user.setCity(userDTO.getCity());
            user.setBio(userDTO.getBio());
            user.setAvatarUrl(userDTO.getAvatarUrl());
            user.setCoverUrl(userDTO.getCoverUrl());
            user.setUpdatedAt(LocalDate.now());
            User savedUser = userRepository.save(user);
            savedUser = reputationService.updateUserReputation(savedUser);
            return userMapper.userToUserDTO(savedUser);
        }).orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO patchUserAvatar(String userId, String avatarUrl) throws RadioNotFoundException {
        return userRepository.findById(userId).map(user -> {
            user.setAvatarUrl(avatarUrl);
            user.setUpdatedAt(LocalDate.now());
            User savedUser = userRepository.save(user);
            savedUser = reputationService.updateUserReputation(savedUser);
            return userMapper.userToUserDTO(savedUser);
        }).orElseThrow(RadioNotFoundException::new);
    }

    @Override
    public UserDTO patchUserCover(String userId, String coverUrl) throws RadioNotFoundException {
        return userRepository.findById(userId).map(user -> {
            user.setCoverUrl(coverUrl);
            User savedUser = userRepository.save(user);
            savedUser = reputationService.updateUserReputation(savedUser);
            return userMapper.userToUserDTO(savedUser);
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

    @Override
    public Flux<StationDTO> getStationByUserId(String userId) {
        return stationRepository.findByOwnerId(userId).map(station -> stationMapper.stationToStationDTO(station));
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            User currentUser = user.get();
            if(passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
                currentUser.setPassword(passwordEncoder.encode(newPassword));

                userRepository.save(currentUser);
                return true;
            }

            return false;
        }
        return false;
    }

    @Override
    public UserDTO forgotPassword(String email) {
        Optional<User> existedUser = userRepository.findFirstByEmail(email);

        if(existedUser.isPresent()) {
            User user = existedUser.get();
            String token = UUID.randomUUID().toString();
            LocalDate tokenExpiryDate = LocalDate.now().plusDays(1);
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiryDate(tokenExpiryDate);
            return userMapper.userToUserDTO(userRepository.save(user));
        } else {
            throw new RadioNotFoundException();
        }
    }

    @Override
    public UserDTO resetPassword(String resetPasswordToken, String newPassword) {
        Optional<User> user = userRepository.findByResetPasswordTokenAndResetPasswordTokenExpiryDateAfter(resetPasswordToken, LocalDate.now());
        if(user.isPresent()) {
            User resettingPasswordUser = user.get();
            resettingPasswordUser.setResetPasswordTokenExpiryDate(null);
            resettingPasswordUser.setResetPasswordToken(null);
            user.get().setPassword(passwordEncoder.encode(newPassword));
            return userMapper.userToUserDTO(userRepository.save(resettingPasswordUser));
        } else {
            throw new RadioBadRequestException("reset password fail.");
        }
    }

    @Override
    public Flux<StationDTO> getStationsByUserIdAndPrivacy(String userId, StationPrivacy privacy) {
        return stationRepository.findByOwnerIdAndPrivacy(userId, privacy)
                .map(station -> stationMapper.stationToStationDTO(station));
    }

    @Override
    public User getAnonymousUser(String cookieId) {
        if (cookieId.isEmpty() || cookieId.equals(constant.getDefaultCookie())) {
            User user = new User();
            String unique = UUID.randomUUID().toString();
            user.setUsername(unique);
            user.setName(unique);
            user.setCookieId(unique);
            return userRepository.save(user);
        }
        Optional<User> user = userRepository.findByCookieId(cookieId);
        if (!user.isPresent()) {
            throw new RadioNotFoundException();
        }
        return user.get();
    }
}
