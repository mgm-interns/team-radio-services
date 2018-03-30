package com.mgmtp.radio.dto.conversation;

import com.mgmtp.radio.domain.conversation.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Data
public class ConversationDTO {
    String id;
    List<Message> messages;
    String uid;
    LocalDate createdAt;
}
