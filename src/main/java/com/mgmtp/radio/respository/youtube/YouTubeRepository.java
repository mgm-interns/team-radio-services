package com.mgmtp.radio.respository.youtube;

import com.mgmtp.radio.domain.youtube.YouTubeVideo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface YouTubeRepository extends MongoRepository<YouTubeVideo, String> {
    YouTubeVideo findByTitle(String title);
}
