package com.mgmtp.radio.support;

import com.mgmtp.radio.config.MailgunConfig;
import com.mgmtp.radio.domain.mail.Email;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class MailgunHelper {

    private final MailgunConfig mailgunConfig;

    public MailgunHelper(MailgunConfig mailgunConfig) {
        this.mailgunConfig = mailgunConfig;
    }

    public ClientResponse sendMail(Email email) {
        WebResource webResource = mailgunConfig.getWebResource();
        MultivaluedMapImpl formData = new MultivaluedMapImpl();

        formData.add("from", mailgunConfig.getFrom());
        formData.add("subject", email.getSubject());
        formData.add("to", email.getTo());

        email.getCc().stream().forEach(ccAddress -> formData.add("cc", ccAddress));
        email.getBcc().stream().forEach(bccAddress -> formData.add("bcc", bccAddress));
        formData.add("html", email.getContent());

        return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
    }
}
