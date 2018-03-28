package com.mgmtp.radio.aop;

import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.event.MailgunEventEndPoint;
import com.mgmtp.radio.sdo.EventDataKeys;
import com.mgmtp.radio.support.MailgunHelper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class UserAspect {
    private final MessageChannel mailChannel;

    public UserAspect(MessageChannel mailChannel) {
        this.mailChannel = mailChannel;
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.register(..))", returning = "userInfo")
    public void sendMailRegisterSuccess(UserDTO userInfo){
        Map<String, Object> mailParam = new HashMap<>();
        mailParam.put(EventDataKeys.user_id.name(), userInfo.getId());
        mailParam.put(EventDataKeys.event_id.name(), "sendMailRegister");
        mailParam.put(MailgunEventEndPoint.SUBJECT_KEY, MailgunHelper.REGISTER_SUBJECT_KEY);
        mailParam.put(MailgunEventEndPoint.TO_KEY, userInfo.getEmail());
        mailParam.put(MailgunEventEndPoint.CC_LIST_KEY, Collections.emptyList());
        mailParam.put(MailgunEventEndPoint.BCC_LIST_KEY, Collections.emptyList());
        mailParam.put(MailgunEventEndPoint.CONTENT_PARAMS_KEY, Arrays.asList(userInfo.getName(), userInfo.getUsername()));

        mailChannel.send(new GenericMessage<>(mailParam));
    }
}
