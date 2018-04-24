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
    MessageChannel forgotPasswordChannel;

    public UserAspect(MessageChannel registerChannel,
                      MessageChannel forgotPasswordChannel) {
        this.registerChannel = registerChannel;
        this.forgotPasswordChannel = forgotPasswordChannel;
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.register(..))", returning = "userInfo")
    public void sendMailRegisterSuccess(UserDTO userInfo){
        Map<String, Object> mailParam = new HashMap<>();
        mailParam.put(EventDataKeys.user_id.name(), userInfo.getId());
        mailParam.put(EventDataKeys.event_id.name(), SubscriptionEvents.register.name());

        registerChannel.send(new GenericMessage<>(mailParam));
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.forgotPassword(..))", returning = "userInfo")
    public void sendEmailForgotPassword(UserDTO userInfo) {
        Map<String, Object> mailParam = new HashMap<>();
        mailParam.put(EventDataKeys.user_id.name(), userInfo.getId());
        mailParam.put(EventDataKeys.event_id.name(), SubscriptionEvents.forgot_password.name());

        forgotPasswordChannel.send(new GenericMessage<>(mailParam));
    }

    @AfterReturning(value = "execution(* com.mgmtp.radio.service.user.UserServiceImpl.resetPassword(..))", returning = "userInfo")
    public void sendEmailAfterResetPassword(UserDTO userInfo) {
        Map<String, Object> mailParam = new HashMap<>();
        mailParam.put(EventDataKeys.user_id.name(), userInfo.getId());
        mailParam.put(EventDataKeys.event_id.name(), SubscriptionEvents.reset_password.name());

        forgotPasswordChannel.send(new GenericMessage<>(mailParam));
    }
}
