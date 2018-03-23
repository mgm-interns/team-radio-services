package com.mgmtp.radio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:local.properties"}, ignoreResourceNotFound = true)
@PropertySource(value = {"classpath:message.properties"})
public class RadioConfig {

}
