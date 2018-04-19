package com.mgmtp.radio.dto.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@NoArgsConstructor
@Data
public class MessageDTO {
    String id;
    SenderDTO sender;
    @NotEmpty
    String content;
    @NotEmpty
    String stationId;
    LocalDate createdAt;
}
