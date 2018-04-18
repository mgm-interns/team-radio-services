package com.mgmtp.radio.domain.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Data
@Document
public class Sender {
    private String userId;
    private String username;
    private String avatarUrl;
}
