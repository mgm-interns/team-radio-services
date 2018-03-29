package com.mgmtp.radio.domain.mail;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Email {

    private String subject;

    private String to;

    private List<String> cc;

    private List<String> bcc;

    private List<String> contentParams;
}
