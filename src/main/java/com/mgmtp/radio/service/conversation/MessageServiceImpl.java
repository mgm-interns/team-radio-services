package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.domain.conversation.FromUser;
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

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserHelper userHelper;
    private final Constant constant;


    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper, UserHelper userHelper, Constant constant) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userHelper = userHelper;
        this.constant = constant;
    }

    @Override
    public Mono<MessageDTO> create(String stationId, User user, MessageDTO messageDTO) {
        FromUser fromUser = userHelper.convertUserToFromUser(user);
        String userNameFormat = "%-"+constant.getUsernameLimit()+"s";
        String avatarUrlFormat = "%-"+constant.getAvatarLimit()+"s";
        fromUser.setUsername(String.format(userNameFormat, fromUser.getUsername()));
        fromUser.setAvatarUrl(String.format(avatarUrlFormat, fromUser.getAvatarUrl()));
        messageDTO.setStationId(stationId);
        messageDTO.setCreatedAt(LocalDate.now());

        Message message = messageMapper.messageDtoToMessage(messageDTO);
        message.setFrom(fromUser);
        return messageRepository.save(message).map(messageMapper::messageToMessageDTO).switchIfEmpty(Mono.error(new RadioException()));
    }

    @Override
    public Flux<MessageDTO> findByStationId(String stationId) {
        return messageRepository.findByStationId(stationId).map(messageMapper::messageToMessageDTO);
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
    public Mono<MessageDTO> save(MessageDTO messageDTO) {
        return messageRepository.save(messageMapper.messageDtoToMessage(messageDTO)).map(messageMapper::messageToMessageDTO).switchIfEmpty(Mono.error(new RadioException()));
    }
}
