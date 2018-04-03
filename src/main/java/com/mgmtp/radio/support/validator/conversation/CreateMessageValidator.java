package com.mgmtp.radio.support.validator.conversation;

import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.service.conversation.MessageService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Component
public class CreateMessageValidator implements Validator {

    private final MessageSource messageSource;
    private final MessageService messageService;

    public CreateMessageValidator(MessageSource messageSource, MessageService messageService) {
        this.messageSource = messageSource;
        this.messageService = messageService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return MessageDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MessageDTO messageDTO = (MessageDTO) target;
        this.validateUnique(messageDTO, errors);
    }

    private void validateUnique(MessageDTO messageDTO, Errors errors) {
        if (isMessageExisted(messageDTO.getId())) {
            errors.rejectValue("id", "", messageSource.getMessage("validation.error.unique", new String[]{"Message"}, Locale.getDefault()));
        }
    }

    private boolean isMessageExisted(String id) {
        return messageService.existsById(id).block();
    }
}
