package com.mgmtp.radio.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;

@Document(collection = "radio_client_details")
@Data
@NoArgsConstructor
public class RadioClientDetails implements ClientDetails {

    @Id
    private String clientId;
    private String clientSecret;
    private Set<String> scope = Collections.emptySet();
    private Set<String> resourceIds = Collections.emptySet();
    private Set<String> authorizedGrantTypes = Collections.emptySet();
    private Set<String> registeredRedirectUris;
    private List<GrantedAuthority> authorities = Collections.emptyList();
    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;
    private Map<String, Object> additionalInformation = new LinkedHashMap<>();
    private Set<String> autoApproveScopes;


    @PersistenceConstructor
    public RadioClientDetails(final String clientId,
                              final String clientSecret,
                              final Set<String> scope,
                              final Set<String> resourceIds,
                              final Set<String> authorizedGrantTypes,
                              final Set<String> registeredRedirectUris,
                              final List<GrantedAuthority> authorities,
                              final Integer accessTokenValiditySeconds,
                              final Integer refreshTokenValiditySeconds,
                              final Map<String, Object> additionalInformation,
                              final Set<String> autoApproveScopes) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.resourceIds = resourceIds;
        this.authorizedGrantTypes = authorizedGrantTypes;
        this.registeredRedirectUris = registeredRedirectUris;
        this.authorities = authorities;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        this.additionalInformation = additionalInformation;
        this.autoApproveScopes = autoApproveScopes;
    }

    @Override
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUris;
    }

    @Override
    public boolean isAutoApprove(final String scope) {
        if (autoApproveScopes == null) {
            return false;
        }
        for (String auto : autoApproveScopes) {
            if (auto.equals("true") || scope.matches(auto)) {
                return true;
            }
        }
        return false;
    }
}
