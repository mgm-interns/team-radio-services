package com.mgmtp.radio.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;

@Configuration
@PropertySource(value = {"classpath:local.properties"}, ignoreResourceNotFound = true)
@PropertySource(value = {"classpath:messages.properties"})
@PropertySource(value = {"classpath:constants.properties"})
public class RadioConfig {

    @Value("${application.name}")
    private String APPLICATION_NAME;

    public String getApplicationName() {
        return APPLICATION_NAME;
    }

    @Bean
    MessageSourceAccessor getMessageSourceAccessor(MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource);
    }
}
