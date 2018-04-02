package com.mgmtp.radio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YouTubeConfig {
    @Value("${youtube.api.key}")
    private String YOUTUBE_API_KEY;

    @Value("${youtube.url}")
    private String YOUTUBE_URL;

    public String getApiKey() {
        return YOUTUBE_API_KEY;
    }

    public String getUrl() {
        return YOUTUBE_URL;
    }

    public void setApiKey(String YOUTUBE_API_KEY) {
        this.YOUTUBE_API_KEY = YOUTUBE_API_KEY;
    }

    public void setUrl(String YOUTUBE_URL) {
        this.YOUTUBE_URL = YOUTUBE_URL;
    }
}
