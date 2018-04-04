package com.mgmtp.radio.domain.mail;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Email {

    private String subject;

    private String to;

    private List<String> cc;

    private List<String> bcc;

    private String content;
}
