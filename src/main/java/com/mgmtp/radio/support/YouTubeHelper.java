package com.mgmtp.radio.support;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.mgmtp.radio.config.RadioConfig;
import com.mgmtp.radio.config.YouTubeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class YouTubeHelper {
    @Autowired
    YouTubeConfig youTubeConfig;

    @Autowired
    RadioConfig radioConfig;

    private YouTube youTube;

    public Video getYouTubeVideoById(String videoId) {
        try {
            VideoListResponse response;
            youTube = getYouTube();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("part", "id,snippet,contentDetails");
            parameters.put("id", videoId);

            YouTube.Videos.List videosListByIdRequest = youTube.videos().list(parameters.get("part").toString());
            videosListByIdRequest.setKey(youTubeConfig.getApiKey());
            if (parameters.containsKey("id") && parameters.get("id").isEmpty()) {
                videosListByIdRequest.setId(parameters.get("id"));
            }

            response = videosListByIdRequest.execute();

            return response.getItems().get(0);

        } catch (IOException e) {
            e.printStackTrace();
            return new Video();
        }
    }

    private YouTube getYouTube() {
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                httpRequest -> {
                }).setApplicationName(radioConfig.getApplicationName()).build();

        return youTube;
    }
}
