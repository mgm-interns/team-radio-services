package com.mgmtp.radio.security;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.respository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RadioUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public RadioUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(usernameOrEmail);
        if(!user.isPresent()) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        if (!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("User %s does not exist!", usernameOrEmail));
        }
        return user.get();
    }
}
