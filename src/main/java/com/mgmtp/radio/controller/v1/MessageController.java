package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.service.conversation.MessageService;
import com.mgmtp.radio.support.validator.conversation.CreateMessageValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping(MessageController.BASE_URL)
public class MessageController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/stations/{stationId}/messages";

    private final MessageService messageService;
    private final CreateMessageValidator createMessageValidator;

    public MessageController(MessageService messageService, CreateMessageValidator createMessageValidator) {
        this.messageService = messageService;
        this.createMessageValidator = createMessageValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(this.createMessageValidator);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<RadioSuccessResponse<MessageDTO>> store(@PathVariable(value = "stationId") String stationId, @Validated @RequestBody MessageDTO messageDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.error(new RadioBadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }

        // Todo: set from current user.
        // User user = getCurrentUser();
        User user = new User();
        user.setId("001");
        user.setUsername("john doe");
        user.setAvatarUrl("http://johndoe.com/avatar");

        return messageService.create(stationId, user, messageDTO).map(RadioSuccessResponse::new);
    }

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<RadioSuccessResponse<MessageDTO>> getAll(@PathVariable(value = "stationId") String stationId) {
        return messageService.findByStationId(stationId).map(RadioSuccessResponse::new);
    }

}

