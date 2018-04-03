package com.mgmtp.radio.mapper.conversation;

import com.mgmtp.radio.domain.conversation.Message;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.mapper.decorator.MessageMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
@DecoratedWith(MessageMapperDecorator.class)
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    MessageDTO messageToMessageDTO(Message message);

    Message messageDtoToMessage(MessageDTO messageDTO);
}
