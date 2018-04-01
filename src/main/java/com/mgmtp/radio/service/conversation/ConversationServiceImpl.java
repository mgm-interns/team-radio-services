package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.domain.conversation.Conversation;
import com.mgmtp.radio.dto.conversation.ConversationDTO;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.conversation.ConversationMapper;
import com.mgmtp.radio.respository.conversation.ConversationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;

    public ConversationServiceImpl(ConversationRepository conversationRepository, ConversationMapper conversationMapper) {
        this.conversationRepository = conversationRepository;
        this.conversationMapper = conversationMapper;
    }

    @Override
    public Mono<ConversationDTO> create(ConversationDTO conversationDTO) {
        conversationDTO.setCreatedAt(LocalDate.now());
        if (conversationDTO.getId() == null || conversationDTO.getId().isEmpty()) {
            conversationDTO.setId(UUID.randomUUID().toString());
        }
        Conversation conversation = conversationMapper.conversationDtoToConversation(conversationDTO);
        return conversationRepository.save(conversation).map(conversationMapper::conversationToConversationDTO).switchIfEmpty(Mono.error(new RadioException()));
    }

    @Override
    public Flux<ConversationDTO> findByUid(String uid) {
        return null;
    }

    @Override
    public Mono<ConversationDTO> findById(String id) {
        return conversationRepository.findById(id).map(conversationMapper::conversationToConversationDTO).switchIfEmpty(Mono.error(new RadioNotFoundException()));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return conversationRepository.findById(id).map(conversation -> true).switchIfEmpty(Mono.just(false));
    }
}
