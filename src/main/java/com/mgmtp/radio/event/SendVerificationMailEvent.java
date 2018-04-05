package com.mgmtp.radio.event;

import com.mgmtp.radio.domain.mail.Email;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.respository.user.UserRepository;
import com.mgmtp.radio.sdo.EventDataKeys;
import com.mgmtp.radio.sdo.SubscriptionEvents;
import com.mgmtp.radio.support.MailgunHelper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Log4j2
@MessageEndpoint
public class SendVerificationMailEvent extends BaseEventMessageEndpoint {

    private final MailgunHelper mailgunHelper;

    private final MessageSource messageSource;

    private final UserRepository userRepository;

    @Value("classpath:mailTemplate/register_template.html")
    private Resource registerTemplateMail;

    public SendVerificationMailEvent(MailgunHelper mailgunHelper,
                                     MessageSource messageSource,
                                     UserRepository userRepository) {
        this.mailgunHelper = mailgunHelper;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @ServiceActivator(inputChannel = "registerChannel")
    @Override
    public void receive(Map<String, Object> message) {
        super.receive(message);
    }

    @Override
    protected void process(Map<String, Object> message) {
        String userId = message.get(EventDataKeys.user_id.name()).toString();
        String eventId = message.get(EventDataKeys.event_id.name()).toString();

        boolean isValid = isValid(userId, eventId);

        if(!isValid) {
            return;
        }

        Optional<User> user = userRepository.findById(userId);

        try {
            if (user.isPresent() && !StringUtils.isEmpty(user.get().getEmail())) {
                String mailContent = String.format(IOUtils.toString(registerTemplateMail.getInputStream()),
                        user.get().getName(), user.get().getUsername());

                String subject = messageSource.getMessage("mail.user_verification.subject",
                        new String[]{},
                        Locale.getDefault());

                Email email = Email.builder()
                        .subject(subject)
                        .content(mailContent)
                        .to(user.get().getEmail())
                        .build();
                mailgunHelper.sendMail(email);
            }
        } catch (IOException e) {
            log.error("Send mail failed!", e);
        }
    }

    private boolean isValid(String userId, String eventId) {
        return (!StringUtils.isEmpty(userId)
                && !StringUtils.isEmpty(eventId)
                && eventId.equals(SubscriptionEvents.register.name()));
    }
}
