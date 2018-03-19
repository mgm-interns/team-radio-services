package com.mgmtp.radio.config;

import com.mgmtp.radio.security.RadioUserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired(required = false)
    CORSFilter corsFilter;

    @Autowired(required = false)
    SwaggerConfig swaggerConfig;

    @Autowired
    @Lazy
    RadioUserDetailsManager radioUserDetailsManager;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        if (corsFilter != null) {
            http.addFilterBefore(corsFilter, ChannelProcessingFilter.class);
        }

        http
                .csrf().disable()
                .requestMatchers().antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").failureUrl("/login").permitAll()
                .and()
                .logout().permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        if (swaggerConfig != null || corsFilter != null) {
            web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        }

        if (swaggerConfig != null) {
            web.ignoring().antMatchers(HttpMethod.GET, "/api-docs/**");
        }

        web.ignoring().antMatchers(HttpMethod.GET, "/health");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(getUserDetailsManager());
    }

    @Bean
    public UserDetailsService getUserDetailsManager() {
        return radioUserDetailsManager;
    }

    @Bean(name="authenticationManager")
    @Lazy
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
