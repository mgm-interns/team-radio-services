package com.mgmtp.radio.mapper.conversation;

import com.mgmtp.radio.domain.conversation.Sender;
import com.mgmtp.radio.dto.conversation.SenderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SenderMapper {

    SenderMapper INSTANCE = Mappers.getMapper(SenderMapper.class);

    SenderDTO senderToSenderDTO(Sender sender);

    Sender senderDTOToSender(SenderDTO senderDTO);
}
