package com.mgmtp.radio.aop;

import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.sdo.EventDataKeys;
import com.mgmtp.radio.sdo.SubscriptionEvents;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class UserAspect {

    MessageChannel registerChannel;

    public UserAspect(MessageChannel registerChannel) {
        this.registerChannel = registerChannel;
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.register(..))", returning = "userInfo")
    public void sendMailRegisterSuccess(UserDTO userInfo){
        Map<String, Object> mailParam = new HashMap<>();
        mailParam.put(EventDataKeys.user_id.name(), userInfo.getId());
        mailParam.put(EventDataKeys.event_id.name(), SubscriptionEvents.register.name());

        registerChannel.send(new GenericMessage<>(mailParam));
    }
}
