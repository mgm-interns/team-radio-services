package com.mgmtp.radio.schedule;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.dto.conversation.FromUserDTO;
import com.mgmtp.radio.service.conversation.MessageService;
import com.mgmtp.radio.service.user.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UpdateConversationAvatarJob {

    private final UserService userService;
    private final MessageService messageService;
    private final Constant constant;

    public UpdateConversationAvatarJob(UserService userService, MessageService messageService, Constant constant) {
        this.userService = userService;
        this.messageService = messageService;
        this.constant = constant;
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void scheduleUpdateMessageTask() {
        LocalDate today = LocalDate.now();

        String userNameFormat = "%-"+constant.getAvatarLimit()+"s";
        String avatarUrlFormat = "%-"+constant.getAvatarLimit()+"s";

        userService.findUserByUpdatedAt(today).forEach(userDTO -> {
            FromUserDTO fromUserDTO = new FromUserDTO();
            fromUserDTO.setId(userDTO.getId());
            fromUserDTO.setUsername(String.format(userNameFormat, userDTO.getUsername()));
            fromUserDTO.setAvatarUrl(String.format(avatarUrlFormat, userDTO.getAvatarUrl()));
            messageService.findByFromUserId(userDTO.getId()).flatMap(messageDTO -> {
                messageDTO.setFrom(fromUserDTO);
                return messageService.save(messageDTO);
            }).subscribe();
        });
    }
}
