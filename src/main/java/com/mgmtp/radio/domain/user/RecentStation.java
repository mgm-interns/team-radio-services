package com.mgmtp.radio.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "recent_station")
public class RecentStation {
    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String stationId;

    private LocalDateTime joinedTime;
}
