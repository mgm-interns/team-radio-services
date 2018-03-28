package com.mgmtp.radio.config;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@PropertySource(value = "classpath:local.properties")
@ConfigurationProperties(prefix = "mailgun")
public class MailgunConfig {
    @NotBlank
    private String api;

    @NotBlank
    private String domain;

    @NotBlank
    private String from;

    @NotBlank
    private String registerSubject;

    @Bean
    public WebResource getWebResource() {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", this.api));
        WebResource webResource = client
                .resource("https://api.mailgun.net/v3/" + this.domain + "/messages");

        return webResource;
    }
}
