package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.sdo.SongStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Data
public class SongDTO {
	String id;
	String songId;
	String source;
	SongStatus status;
	boolean skipped;
	String url;
	String title;
	String thumbnail;
	long duration;
    UserDTO creatorId;
    int upVoteCount;
    int downVoteCount;
	List<UserDTO> upvoteUserList = Collections.emptyList();
	List<UserDTO> downvoteUserList = Collections.emptyList();
	String message;
	LocalDate createdAt;
}
