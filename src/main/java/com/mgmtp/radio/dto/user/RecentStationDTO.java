package com.mgmtp.radio.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecentStationDTO {

    private String userId;

    private String stationId;

    private LocalDateTime createdAt;
}
