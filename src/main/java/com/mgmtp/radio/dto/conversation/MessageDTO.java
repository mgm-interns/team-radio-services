package com.mgmtp.radio.dto.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class MessageDTO {
    String id;
    UserDTO from;
    UserDTO to;
    String message;
    String uid;
    LocalDate createdAt;
}
