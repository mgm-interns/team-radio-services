package com.mgmtp.radio.dto.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SenderDTO {
    String userId;
    String username;
    String avatarUrl;
}
