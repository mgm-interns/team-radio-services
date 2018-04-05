package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.domain.conversation.FromUser;
import com.mgmtp.radio.domain.conversation.Message;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.mapper.conversation.MessageMapper;
import com.mgmtp.radio.respository.conversation.MessageRepository;
import com.mgmtp.radio.support.UserHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserHelper userHelper;
    @Value("${user.limit.username}")
    private String usernameLimit;
    @Value("${user.limit.avatar}")
    private String avatarLimit;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper, UserHelper userHelper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userHelper = userHelper;
    }

    @Override
    public Mono<MessageDTO> create(String stationId, User user, MessageDTO messageDTO) {
        FromUser fromUser = userHelper.convertUserToFromUser(user);
        String userNameFormat = "%-"+usernameLimit+"s";
        String avatarUrlFormat = "%-"+avatarLimit+"s";
        fromUser.setUsername(String.format(userNameFormat, fromUser.getUsername()));
        fromUser.setAvatarUrl(String.format(avatarUrlFormat, fromUser.getAvatarUrl()));
        messageDTO.setStationId(stationId);
        messageDTO.setCreatedAt(LocalDate.now());

        Message message = messageMapper.messageDtoToMessage(messageDTO);
        message.setFrom(fromUser);
        return messageRepository.save(message).map(messageMapper::messageToMessageDTO).switchIfEmpty(Mono.error(new RadioException()));
    }

    @Override
    public Flux<MessageDTO> findByStationId(String conversationId) {
        return messageRepository.findByStationId(conversationId).map(messageMapper::messageToMessageDTO);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return messageRepository.existsById(id);
    }

    @Override
    public Flux<MessageDTO> findByFromUserId(String id) {
        return messageRepository.findByFrom_Id(id).map(messageMapper::messageToMessageDTO);
    }

    @Override
    public Mono<MessageDTO> save(Message message) {
        return messageRepository.save(message).map(messageMapper::messageToMessageDTO).switchIfEmpty(Mono.error(new RadioException()));
    }
}
