package com.mgmtp.radio.domain.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FromUser {
    private String id;
    private String username;
    private String avatarUrl;
}
