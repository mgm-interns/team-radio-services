package com.mgmtp.radio.mapper.decorator;

import com.mgmtp.radio.domain.conversation.Message;
import com.mgmtp.radio.dto.conversation.FromUserDTO;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.mapper.conversation.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class MessageMapperDecorator implements MessageMapper {

    @Autowired
    @Qualifier("delegate")
    private MessageMapper delegate;

    @Override
    public MessageDTO messageToMessageDTO(Message message) {
        MessageDTO messageDTO = delegate.messageToMessageDTO(message);
        FromUserDTO fromUserDTO = messageDTO.getFrom();
        fromUserDTO.setUsername(fromUserDTO.getUsername().trim());
        fromUserDTO.setAvatarUrl(fromUserDTO.getAvatarUrl().trim());
        messageDTO.setFrom(fromUserDTO);
        return messageDTO;
    }

}
