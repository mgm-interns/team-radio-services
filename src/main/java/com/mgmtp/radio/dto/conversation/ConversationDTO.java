package com.mgmtp.radio.dto.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@Data
public class ConversationDTO {
    String id;
    @NotNull
    String uid;
    LocalDate createdAt;
}
