package com.mgmtp.radio.domain.station;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NowPlaying {
    private String songId;
    private String title;
    private String url;
    private long startingTime;
    private long duration;
    private String thumbnail;
    private String message;
    private boolean isEnded;
    private long skipTimeLeft;
    private boolean skipped;
}
