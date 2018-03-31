package com.mgmtp.radio.dto.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Data
public class ConversationDTO {
    String id;
    List<MessageDTO> messages;
    @NotNull
    String uid;
    LocalDate createdAt;
}
