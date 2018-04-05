package com.mgmtp.radio.bootstrap;

import com.mgmtp.radio.domain.user.RadioClientDetails;
import com.mgmtp.radio.domain.user.Role;
import com.mgmtp.radio.respository.user.RadioClientDetailsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class Bootstrap implements CommandLineRunner {

    private RadioClientDetailsRepository radioClientDetailsRepository;

    @Value("${radio.client.id}")
    String clientId;

    @Value("${radio.client.secret}")
    String clientSecret;

    public Bootstrap(RadioClientDetailsRepository radioClientDetailsRepository) {
        this.radioClientDetailsRepository = radioClientDetailsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        //createClientDetails();
    }

    void createClientDetails() {

        RadioClientDetails radioClientDetails = new RadioClientDetails();
        radioClientDetails.setClientId(clientId);
        radioClientDetails.setClientSecret(clientSecret);
        radioClientDetails.setScope(new HashSet<>(Arrays.asList("read", "write")));
        radioClientDetails.setResourceIds(new HashSet<>(Arrays.asList("radio-resource")));
        radioClientDetails.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList("authorization_code", "implicit")));
        radioClientDetails.setRegisteredRedirectUris(new HashSet<>(Arrays.asList("https://teamrad.io/")));
        Role userRole = new Role();
        userRole.setAuthority("USER");
        radioClientDetails.setAuthorities(new ArrayList<>(Arrays.asList(userRole)));
        Integer expiry = 30 * 24 * 60 * 1000;
        radioClientDetails.setAccessTokenValiditySeconds(expiry);
        radioClientDetails.setRefreshTokenValiditySeconds(expiry);
        radioClientDetailsRepository.save(radioClientDetails);
    }
}
