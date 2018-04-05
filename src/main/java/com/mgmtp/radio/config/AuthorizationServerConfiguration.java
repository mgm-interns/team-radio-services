package com.mgmtp.radio.config;

import com.mgmtp.radio.security.RadioApprovalStore;
import com.mgmtp.radio.security.RadioClientDetailsService;
import com.mgmtp.radio.security.RadioTokenStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private RadioTokenStore radioTokenStore;

    private RadioClientDetailsService radioClientDetailsService;

    private RadioApprovalStore radioApprovalStore;

    private AuthenticationManager authenticationManager;



    public AuthorizationServerConfiguration(RadioTokenStore radioTokenStore, RadioClientDetailsService radioClientDetailsService, RadioApprovalStore radioApprovalStore, AuthenticationManager authenticationManager) {
        this.radioTokenStore = radioTokenStore;
        this.radioClientDetailsService = radioClientDetailsService;
        this.radioApprovalStore = radioApprovalStore;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(radioClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(radioTokenStore)
                .approvalStore(radioApprovalStore)
                .authenticationManager(authenticationManager);
    }

    @Primary
    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(radioTokenStore);
        return tokenServices;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients(); // here
    }


}
