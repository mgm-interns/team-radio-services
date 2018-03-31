package com.mgmtp.radio.mapper.conversation;

import com.mgmtp.radio.domain.conversation.Conversation;
import com.mgmtp.radio.dto.conversation.ConversationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConversationMapper {
    ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);

    @Mapping(target = "messages", ignore = true)
    ConversationDTO conversationToConversationDTO(Conversation conversation);

    Conversation conversationDtoToConversation(ConversationDTO conversationDTO);
}
