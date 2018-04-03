package com.mgmtp.radio.schedule;

import com.mgmtp.radio.dto.conversation.FromUserDTO;
import com.mgmtp.radio.mapper.conversation.MessageMapper;
import com.mgmtp.radio.service.conversation.MessageService;
import com.mgmtp.radio.service.user.UserService;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UpdateConversationAvatarJob {

    private final UserService userService;
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final Environment env;

    public UpdateConversationAvatarJob(UserService userService, MessageService messageService, MessageMapper messageMapper, Environment env) {
        this.userService = userService;
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.env = env;
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void scheduleUpdateMessageTask() {
        LocalDate today = LocalDate.now();

        String userNameFormat = "%-"+env.getProperty("user.limit.username")+"s";
        String avatarUrlFormat = "%-"+env.getProperty("user.limit.avatar")+"s";

        userService.findUserByUpdatedAt(today).forEach(userDTO -> {
            FromUserDTO fromUserDTO = new FromUserDTO();
            fromUserDTO.setId(userDTO.getId());
            fromUserDTO.setUsername(String.format(userNameFormat, userDTO.getUsername()));
            fromUserDTO.setAvatarUrl(String.format(avatarUrlFormat, userDTO.getAvatarUrl()));
            messageService.findByFromUserId(userDTO.getId()).flatMap(messageDTO -> {
                messageDTO.setFrom(fromUserDTO);
                return messageService.save(messageMapper.messageDtoToMessage(messageDTO));
            }).subscribe();
        });
    }
}
