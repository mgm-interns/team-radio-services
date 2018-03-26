package com.mgmtp.radio.domain.youtube;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;

@Document(collection = "youtubeSearchCriteria")
@Data
@NoArgsConstructor
public class YouTubeSearchCriteria {
    @Size(min = 5, max = 64, message = "Search term must be between 5 and 64 characters")
    private String queryTerm;

    public String getQueryTerm() {
        return queryTerm;
    }

    public void setQueryTerm(String queryTerm) {
        this.queryTerm = queryTerm;
    }
}
