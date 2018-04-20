package com.mgmtp.radio.config;

import com.mgmtp.radio.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private RadioTokenStore radioTokenStore;

    @Autowired
    @Qualifier("authenticationManagerBean")
    AuthenticationManager authenticationManager;

    private RadioUserDetailsService radioUserDetailsService;

    private PasswordEncoder passwordEncoder;

    @Value("${radio.client.id}")
    String clientId;

    @Value("${radio.client.secret}")
    String clientSecret;

    Integer timeOut = 30 * 24 * 3600;

    private static final String RESOURCE_ID = "radio-resource";

    public AuthorizationServerConfiguration(RadioTokenStore radioTokenStore,
                                            RadioUserDetailsService radioUserDetailsService,
                                            PasswordEncoder passwordEncoder) {
        this.radioTokenStore = radioTokenStore;
        this.radioUserDetailsService = radioUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientId)
                .authorizedGrantTypes("client_credentials", "password", "refresh_token")
                .authorities("USER")
                .scopes("read", "write")
                .resourceIds(RESOURCE_ID)
                .secret(clientSecret).accessTokenValiditySeconds(timeOut);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(radioTokenStore)
                .authenticationManager(authenticationManager);
    }

    @Primary
    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setRefreshTokenValiditySeconds(timeOut);
        tokenServices.setTokenStore(radioTokenStore);
        return tokenServices;
    }
}
