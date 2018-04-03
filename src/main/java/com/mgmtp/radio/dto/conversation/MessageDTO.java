package com.mgmtp.radio.dto.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@Data
public class MessageDTO {
    String id;
    @NotNull
    UserDTO from;
    @NotEmpty
    String content;
    @NotEmpty
    String stationId;
    LocalDate createdAt;
}
