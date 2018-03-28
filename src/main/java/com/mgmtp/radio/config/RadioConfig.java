package com.mgmtp.radio.config;

import com.mgmtp.radio.event.BaseEventMessageEndpoint;
import com.mgmtp.radio.event.MailgunEventEndPoint;
import com.mgmtp.radio.support.MailgunHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.annotation.ServiceActivator;

@Configuration
@PropertySource(value = {"classpath:local.properties"}, ignoreResourceNotFound = true)
@PropertySource(value = {"classpath:messages.properties"})
public class RadioConfig {
    @Bean
    @ServiceActivator(inputChannel = "mailChannel")
    BaseEventMessageEndpoint mailEndpoint(MailgunHelper mailgunHelper) {
        MailgunEventEndPoint mailgunEventEndPoint = new MailgunEventEndPoint(mailgunHelper);

        return mailgunEventEndPoint;
    }
}
