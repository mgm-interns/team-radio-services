package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.dto.reputation.ReputationDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.reputation.ReputationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ReputationController.BASE_URL)
public class ReputationController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/reputation";
    private final ReputationService reputationService;

    public ReputationController(ReputationService reputationService) {
        this.reputationService = reputationService;
    }

    @GetMapping
    public Mono<ReputationDTO> getReputation() throws RadioNotFoundException {
        String userId = getCurrentUser().isPresent() ? getCurrentUser().get().getId() : null;
        if (userId != null) {
            return reputationService.getReputation(userId);
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }
}
