package com.mgmtp.radio.domain.reputation;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "reputation")
public class Reputation {
    @Id
    String id;

    private String userId;

    private int score;

    private boolean isUpdateAvatarAlready;
}
