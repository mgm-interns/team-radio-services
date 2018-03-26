package com.mgmtp.radio.service.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.mgmtp.radio.domain.youtube.YouTubeVideo;
import com.mgmtp.radio.dto.youtube.YouTubeSearchCriteriaDTO;
import com.mgmtp.radio.dto.youtube.YouTubeVideoDTO;
import com.mgmtp.radio.respository.youtube.YouTubeRepository;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//@Service
public class YouTubeServiceImpl implements YouTubeService {
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final long MAX_SEARCH_RESULTS = 5;
    private final YouTubeRepository youTubeRepository;

    public YouTubeServiceImpl(YouTubeRepository youTubeRepository) {
        this.youTubeRepository = youTubeRepository;
    }

    @Override
    public List<YouTubeVideo> fetchVideosByQuery(String queryTerm) {
        List<YouTubeVideo> videos = new ArrayList<YouTubeVideo>();

        try {
            YouTube youTube = getYouTube();

            YouTube.Search.List search = youTube.search().list("id,snippet");

            String apiKey = "AIzaSyD_HCz-IjU056WTFjBgWYmjjg1YnwRPXXM";
            search.setKey(apiKey);
            search.setQ(queryTerm);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/publishedAt,snippet/thumbnails/default/url)");
            search.setMaxResults(MAX_SEARCH_RESULTS);

            // perform the search and parse the results
            SearchListResponse searchListResponse = search.execute();
            List<SearchResult> searchResultList = searchListResponse.getItems();
            if (searchResultList != null) {
                for (SearchResult result : searchResultList) {
                    YouTubeVideo video = new YouTubeVideo();
                    video.setTitle(result.getSnippet().getTitle());
                    video.setUrl(buildVideoURL(result.getId().getVideoId()));
                    video.setThumbnailUrl(result.getSnippet().getThumbnails().getDefault().getUrl());
                    video.setDescription(result.getSnippet().getDescription());

                    videos.add(video);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return videos;
    }

    /* Constructs the URL to play the YouTube video */
    private String buildVideoURL(String videoId) {
        StringBuilder builder = new StringBuilder();
        builder.append(YOUTUBE_URL);
        builder.append(videoId);

        return builder.toString();
    }

    /* Instantiates the YouTube object */
    private YouTube getYouTube() {
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), (request) -> {
        }).setApplicationName("team-radio-services").build();

        return youTube;
    }
}
