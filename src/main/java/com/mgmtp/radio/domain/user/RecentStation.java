package com.mgmtp.radio.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "recent_station")
@CompoundIndex(def = "{'userId':1, 'stationId':1}", unique = true, name = "recent_station_compound_index")
public class RecentStation {
    @Id
    private String id;

    private String userId;

    private String stationId;

    private LocalDateTime createdAt;
}
