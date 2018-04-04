package com.mgmtp.radio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:local.properties"}, ignoreResourceNotFound = true)
@PropertySource(value = {"classpath:messages.properties"})
public class RadioConfig {

    @Value("${application.name}")
    private String APPLICATION_NAME;

    public String getApplicationName() {
        return APPLICATION_NAME;
    }
}
