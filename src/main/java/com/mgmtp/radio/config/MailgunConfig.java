package com.mgmtp.radio.config;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Component
@Validated
public class MailgunConfig {

    @Value("${mailgun.api}")
    private String api;

    @Value("${mailgun.domain}")
    private String domain;

    @Value("${mailgun.from}")
    private String from;

    @Bean
    public WebResource getWebResource() {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", this.api));
        WebResource webResource = client
                .resource("https://api.mailgun.net/v3/" + this.domain + "/messages");

        return webResource;
    }
}
