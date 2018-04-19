package com.mgmtp.radio.dto.reputation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReputationDTO {

    private String userId;

    private int score;

    private boolean isUpdateAvatarAlready;
}
