package com.mgmtp.radio.mapper.decorator;

import com.mgmtp.radio.domain.conversation.Conversation;
import com.mgmtp.radio.dto.conversation.ConversationDTO;
import com.mgmtp.radio.mapper.conversation.ConversationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class ConversationMapperDecorator implements ConversationMapper {

    @Autowired
    @Qualifier
    private ConversationMapper delegate;

    @Override
    public ConversationDTO conversationToConversationDTO(Conversation conversation) {
        ConversationDTO conversationDTO = delegate.conversationToConversationDTO(conversation);
        // Todo: convert list string id into list Message object
        return conversationDTO;
    }

}
