package com.mgmtp.radio.dto.youtube;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class YouTubeVideoDTO {
    String title;
    String url;
    String thumbnailUrl;
    String description;
}
