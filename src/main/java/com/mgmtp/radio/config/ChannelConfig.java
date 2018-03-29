package com.mgmtp.radio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ChannelConfig {
    @Bean
    MessageChannel mailChannel() {
        return new DirectChannel();
    }
}
