package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.domain.conversation.Message;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.mapper.conversation.MessageMapper;
import com.mgmtp.radio.respository.conversation.MessageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    public Flux<MessageDTO> findByListId(List<String> listId) {
        return messageRepository.findByIdIn(listId).map(messageMapper::messageToMessageDTO);
    }

    @Override
    public Mono<MessageDTO> create(MessageDTO messageDTO) {
        messageDTO.setCreatedAt(LocalDate.now());
        Message message = messageMapper.messageDtoToMessage(messageDTO);
        return messageRepository.save(message).map(messageMapper::messageToMessageDTO).switchIfEmpty(Mono.error(new RadioException()));
    }
}
