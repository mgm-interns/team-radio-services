package com.mgmtp.radio.dto.conversation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class MessageDTO {
    String id;
    SenderDTO sender;
    @NotEmpty
    String content;
    String stationId;
    LocalDateTime createdAt;
}
