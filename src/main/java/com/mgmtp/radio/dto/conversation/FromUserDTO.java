package com.mgmtp.radio.dto.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FromUserDTO {
    String id;
    String username;
    String avatarUrl;
}
