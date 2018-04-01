package com.mgmtp.radio.support.validator.conversation;

import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.service.conversation.ConversationService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Component
public class CreateMessageValidator implements Validator {

    private final ConversationService conversationService;
    private final MessageSource messageSource;

    public CreateMessageValidator(ConversationService conversationService, MessageSource messageSource) {
        this.conversationService = conversationService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return MessageDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MessageDTO messageDTO = (MessageDTO) target;
        this.validateExists(messageDTO, errors);
    }

    private void validateExists(MessageDTO messageDTO, Errors errors) {
        if (!isConversationExisted(messageDTO.getConversationId())) {
            errors.rejectValue("conversationId", "", messageSource.getMessage("conversation.error.exist.conversationId", new String[]{"Conversation Id"}, Locale.getDefault()));
        }
    }

    private boolean isConversationExisted(String conversationId) {
        return conversationService.existsById(conversationId).block();
    }
}
