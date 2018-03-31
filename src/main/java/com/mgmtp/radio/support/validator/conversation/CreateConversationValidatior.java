package com.mgmtp.radio.support.validator.conversation;

import com.mgmtp.radio.dto.conversation.ConversationDTO;
import com.mgmtp.radio.service.station.StationService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Component
public class CreateConversationValidatior implements Validator {

    private final StationService stationService;
    private final MessageSource messageSource;

    public CreateConversationValidatior(StationService stationService, MessageSource messageSource) {
        this.stationService = stationService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return ConversationDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ConversationDTO conversationDTO = (ConversationDTO) target;
        this.validateExists(conversationDTO, errors);
    }

    private void validateExists(ConversationDTO conversationDTO, Errors errors) {
        if (!isUidExisted(conversationDTO.getUid())) {
            errors.rejectValue("uid", "", messageSource.getMessage("conversation.error.unique.uid", new String[]{}, Locale.getDefault()));
        }

    }

    private boolean isUidExisted(String uid) {
        return stationService.existsById(uid).block();
    }

}