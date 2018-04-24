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
public class SentResetPasswordSuccessfulMailEvent extends BaseEventMessageEndpoint {
    private final MailgunHelper mailgunHelper;

    private final MessageSource messageSource;

    private final UserRepository userRepository;

    @Value("${front_end.url}")
    private String FRONT_END_URL;

    @Value("${front_end.reset_password.url}")
    private String RESET_PASSWORD_URL;

    @Value("classpath:mailTemplate/reset-password-email.html")
    private Resource resetPasswordTemplateMail;

    public SentResetPasswordSuccessfulMailEvent(MailgunHelper mailgunHelper,
                                                MessageSource messageSource,
                                                UserRepository userRepository) {
        this.mailgunHelper = mailgunHelper;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @ServiceActivator(inputChannel = "forgotPasswordChannel")
    @Override
    public void receive(Map<String, Object> message) {
        super.receive(message);
    }

    @Override
    boolean canHandleMessage(Map<String, Object> messageData) {

        if (messageData == null ||
                messageData.get(EventDataKeys.user_id.name()) == null ||
                messageData.get(EventDataKeys.event_id.name()) == null) {
            log.error("Invalid event message data received {}", messageData);
            return false;
        }

        String userId = (String) messageData.get(EventDataKeys.user_id.name());
        String eventId = (String) messageData.get(EventDataKeys.event_id.name());

        return isValid(userId, eventId);
    }

    @Override
    protected void process(Map<String, Object> message) {
        String userId = message.get(EventDataKeys.user_id.name()).toString();

        Optional<User> user = userRepository.findById(userId);

        try {
            if (user.isPresent() && !StringUtils.isEmpty(user.get().getEmail())) {

                String mailContent = IOUtils.toString(resetPasswordTemplateMail.getInputStream())
                        .replace("{{name}}", user.get().getName());

                String subject = messageSource.getMessage("mail.reset_password.subject",
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
                && eventId.equals(SubscriptionEvents.reset_password.name()));
    }
}
