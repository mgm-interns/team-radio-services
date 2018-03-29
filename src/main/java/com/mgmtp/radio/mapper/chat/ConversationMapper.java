package com.mgmtp.radio.mapper.chat;

import com.mgmtp.radio.domain.chat.Conversation;
import com.mgmtp.radio.dto.chat.ConversationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConversationMapper {
    ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);

    ConversationDTO conversationToConversationDTO(Conversation conversation);

    Conversation conversationDtoToConversation(ConversationDTO conversationDTO);
}
