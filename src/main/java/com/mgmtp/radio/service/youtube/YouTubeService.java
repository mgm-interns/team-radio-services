package com.mgmtp.radio.service.youtube;

import com.mgmtp.radio.domain.youtube.YouTubeVideo;
import com.mgmtp.radio.dto.youtube.YouTubeSearchCriteriaDTO;

import java.util.List;

public interface YouTubeService {
    List<YouTubeVideo> fetchVideosByQuery(String queryTerm);
}
