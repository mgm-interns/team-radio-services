package com.mgmtp.radio.dto.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@NoArgsConstructor
@Data
public class MessageDTO {
    String id;
    UserDTO from;
    String content;
    LocalDate createdAt;
}
