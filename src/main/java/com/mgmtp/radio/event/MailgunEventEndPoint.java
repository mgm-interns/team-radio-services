package com.mgmtp.radio.event;

import com.mgmtp.radio.domain.mail.Email;
import com.mgmtp.radio.support.MailgunHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class MailgunEventEndPoint extends BaseEventMessageEndpoint {
    public static final String SUBJECT_KEY = "subject";
    public static final String TO_KEY = "to_list";
    public static final String CC_LIST_KEY = "cc_list";
    public static final String BCC_LIST_KEY = "bcc_list";
    public static final String CONTENT_PARAMS_KEY = "content_params";

    private final MailgunHelper mailgunHelper;

    public MailgunEventEndPoint(MailgunHelper mailgunHelper) {
        this.mailgunHelper = mailgunHelper;
    }

    @Override
    protected void process(Map<String, Object> message) {
        Email email = new Email();
        email.setSubject((String) message.get(SUBJECT_KEY));
        email.setTo((String) message.get(TO_KEY));
        email.setCc((List<String>) message.get(CC_LIST_KEY));
        email.setBcc((List<String>) message.get(BCC_LIST_KEY));
        email.setContentParams((List<String>) message.get(CONTENT_PARAMS_KEY));

        try {
            mailgunHelper.sendMail(email);
        } catch (IOException e) {
            log.error("Send mail failed!", e);
        } catch (IllegalAccessException e) {
            log.error("Send mail failed!", e);
        } catch (InvocationTargetException e) {
            log.error("Send mail failed!", e);
        }
    }
}
