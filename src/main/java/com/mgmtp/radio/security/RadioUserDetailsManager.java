package com.mgmtp.radio.security;

import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.security.service.SecurityContextService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;

@Log4j2
@Component
public class RadioUserDetailsManager implements UserDetailsManager {

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private SecurityContextService securityContextService;

    @Autowired
    public RadioUserDetailsManager(final UserRepository userRepository,
                                   final AuthenticationManager authenticationManager,
                                   final SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.securityContextService = securityContextService;
    }

    @Override
    public void createUser(UserDetails userDetails) {
        validateUserDetails(userDetails);
        userRepository.save(getUser(userDetails));
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        validateUserDetails(userDetails);
        userRepository.save(getUser(userDetails));

    }

    @Override
    public void deleteUser(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> userRepository.delete(user));
    }

    @Override
    public void changePassword(final String oldPassword, final String newPassword) {
        final Authentication currentUser = securityContextService.getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException("Can't change password as no Authentication object found in context " +
                    "for current user.");
        }

        final String username = currentUser.getName();

        // If an authentication manager has been set, re-authenticate the user with the supplied password.
        if (authenticationManager != null) {
            log.debug("Reauthenticating user '"+ username + "' for password change request.");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        } else {
            log.debug("No authentication manager set. Password won't be re-checked.");
        }

        log.debug("Changing password for user '"+ username + "'");

        User existingUser = userRepository.findByUsernameAndPassword(username, oldPassword);
        existingUser.setPassword(newPassword);
        userRepository.save(existingUser);
        securityContextService.setAuthentication(createNewAuthentication(currentUser));
    }


    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).get();
    }

    protected Authentication createNewAuthentication(final Authentication currentAuth) {
        final UserDetails user = loadUserByUsername(currentAuth.getName());

        final UsernamePasswordAuthenticationToken newAuthentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());

        return newAuthentication;
    }


    User getUser(UserDetails userDetails) {
        return (User) userDetails;
    }

    void validateUserDetails(UserDetails user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }

    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Authorities list must not be null");

        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(), "getAuthority() method must return a non-empty string");
        }
    }
}
