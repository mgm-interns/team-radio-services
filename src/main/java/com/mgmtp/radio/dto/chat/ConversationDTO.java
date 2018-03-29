package com.mgmtp.radio.dto.chat;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class ConversationDTO {
    String id;
    UserDTO from;
    UserDTO to;
    String message;
    String uid;
    LocalDate createdAt;
}
