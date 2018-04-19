package com.mgmtp.radio.config;

import com.mgmtp.radio.mapper.conversation.MessageMapper;
import com.mgmtp.radio.mapper.conversation.MessageMapperImpl;
import com.mgmtp.radio.mapper.conversation.MessageMapperImpl_;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfigTests {

    @Bean(name = "delegate")
    public MessageMapper delegate() {
        return new MessageMapperImpl_();
    }

    @Bean(name = "messageMapperImpl")
    public MessageMapper messageMapperImpl () {
        return new MessageMapperImpl();
    }
}
