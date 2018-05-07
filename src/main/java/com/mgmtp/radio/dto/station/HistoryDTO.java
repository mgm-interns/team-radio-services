package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.dto.user.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HistoryDTO {
    String id;
    String stationId;
    String songId;
    String url;
    String title;
    String thumbnail;
    private long duration;
    UserDTO creator;
}
