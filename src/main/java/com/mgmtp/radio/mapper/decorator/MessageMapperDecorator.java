package com.mgmtp.radio.mapper.decorator;

import com.mgmtp.radio.domain.conversation.Message;
import com.mgmtp.radio.dto.conversation.SenderDTO;
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
        SenderDTO senderDTO = messageDTO.getSender();
        senderDTO.setUsername(senderDTO.getUsername().trim());
        senderDTO.setAvatarUrl(senderDTO.getAvatarUrl().trim());
        messageDTO.setSender(senderDTO);
        return messageDTO;
    }

}
