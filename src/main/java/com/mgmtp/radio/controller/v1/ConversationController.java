package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.dto.conversation.ConversationDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.service.conversation.ConversationService;
import com.mgmtp.radio.support.validator.conversation.CreateConversationValidatior;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping(ConversationController.BASE_URL)
public class ConversationController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/stations/me/conversations";

    private final ConversationService conversationService;
    private final CreateConversationValidatior createConversationValidatior;

    public ConversationController(ConversationService conversationService, CreateConversationValidatior createConversationValidatior) {
        this.conversationService = conversationService;
        this.createConversationValidatior = createConversationValidatior;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(this.createConversationValidatior);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<RadioSuccessResponse<ConversationDTO>> store(@Validated @RequestBody ConversationDTO conversationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.error(new RadioBadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        return conversationService.create(conversationDTO).map(RadioSuccessResponse::new);
    }

}
