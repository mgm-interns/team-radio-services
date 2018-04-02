package com.mgmtp.radio.domain.station;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NowPlaying {
    private String songId;
    private String url;
    private long startingTime;
    private int duration;
    private String thumbnail;
    private List<String> messages;
    private boolean isEnded;
}
