package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.domain.conversation.Message;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.mapper.conversation.MessageMapper;
import com.mgmtp.radio.respository.conversation.MessageRepository;
import com.mgmtp.radio.support.UserHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserHelper userHelper;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper, UserHelper userHelper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userHelper = userHelper;
    }

    @Override
    public Flux<MessageDTO> findByListId(List<String> listId) {
        return messageRepository.findByIdIn(listId).map(messageMapper::messageToMessageDTO);
    }

    @Override
    public Mono<MessageDTO> create(User user, MessageDTO messageDTO) {
        if (messageDTO.getId() == null || messageDTO.getId().isEmpty()) {
            messageDTO.setId(UUID.randomUUID().toString());
        }
        messageDTO.setCreatedAt(LocalDate.now());
        Message message = messageMapper.messageDtoToMessage(messageDTO);
        message.setFrom(userHelper.convertUserToUserConversation(user));
        return messageRepository.save(message).map(messageMapper::messageToMessageDTO).switchIfEmpty(Mono.error(new RadioException()));
    }

    @Override
    public Flux<MessageDTO> findByConversationId(String conversationId) {
        return messageRepository.findByConversationId(conversationId).map(messageMapper::messageToMessageDTO);
    }
}
