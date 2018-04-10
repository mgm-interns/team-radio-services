package com.mgmtp.radio.config;

import com.mgmtp.radio.security.RadioUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired(required = false)
    CORSFilter corsFilter;

    @Autowired(required = false)
    SwaggerConfig swaggerConfig;

    @Autowired
    RadioUserDetailsService radioUserDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        if (corsFilter != null) {
            http.addFilterBefore(corsFilter, ChannelProcessingFilter.class);
        }

        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/oauth/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();
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
        return radioUserDetailsService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
