package com.mgmtp.radio.security;

import com.mgmtp.radio.domain.user.RadioClientDetails;
import com.mgmtp.radio.respository.user.RadioClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class RadioClientDetailsService implements ClientDetailsService, ClientRegistrationService {


    private final RadioClientDetailsRepository radioClientDetailsRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RadioClientDetailsService(RadioClientDetailsRepository radioClientDetailsRepository, PasswordEncoder passwordEncoder) {
        this.radioClientDetailsRepository = radioClientDetailsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        try {
            return radioClientDetailsRepository.findByClientId(clientId);
        } catch (IllegalArgumentException e) {
            throw new ClientRegistrationException("No Client Details for client id", e);
        }
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        final RadioClientDetails radioClientDetails = new RadioClientDetails(clientDetails.getClientId(),
                passwordEncoder.encode(clientDetails.getClientSecret()),
                clientDetails.getScope(),
                clientDetails.getResourceIds(),
                clientDetails.getAuthorizedGrantTypes(),
                clientDetails.getRegisteredRedirectUri(),
                (List<GrantedAuthority>) clientDetails.getAuthorities(),
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                clientDetails.getAdditionalInformation(),
                getAutoApproveScopes(clientDetails));

        radioClientDetailsRepository.save(radioClientDetails);
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        final RadioClientDetails radioClientDetails = new RadioClientDetails(clientDetails.getClientId(),
                clientDetails.getClientSecret(),
                clientDetails.getScope(),
                clientDetails.getResourceIds(),
                clientDetails.getAuthorizedGrantTypes(),
                clientDetails.getRegisteredRedirectUri(),
                (List<GrantedAuthority>) clientDetails.getAuthorities(),
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                clientDetails.getAdditionalInformation(),
                getAutoApproveScopes(clientDetails));

        try {
            radioClientDetailsRepository.save(radioClientDetails);
        } catch (Exception e) {
            throw new NoSuchClientException("No such Client Id");
        }
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        try {
            RadioClientDetails radioClientDetails = radioClientDetailsRepository.findByClientId(clientId);
            radioClientDetails.setClientSecret(passwordEncoder.encode(secret));
            radioClientDetailsRepository.save(radioClientDetails);
        } catch (Exception e) {
            throw new NoSuchClientException("No such client id");
        }
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        try {
            radioClientDetailsRepository.deleteByClientId(clientId);
        } catch (Exception e) {
            throw new NoSuchClientException("No such client id");
        }
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        final List<RadioClientDetails> radioClientDetails = radioClientDetailsRepository.findAll();
        return radioClientDetails.stream()
                .map(toClientDetails())
                .collect(Collectors.toList());
    }

    private Set<String> getAutoApproveScopes(final ClientDetails clientDetails) {
        if (clientDetails.isAutoApprove("true")) {
            return  new HashSet<>(Arrays.asList("true"));
        }

        return clientDetails.getScope().stream()
                .filter(ByAutoApproveOfScope(clientDetails))
                .collect(Collectors.toSet());
    }

    private Predicate<String> ByAutoApproveOfScope(final ClientDetails clientDetails) {

        return scope -> clientDetails.isAutoApprove(scope);
    }

    private Function<RadioClientDetails, ClientDetails> toClientDetails() {
        return radioClientDetails -> radioClientDetails;
    }
}
