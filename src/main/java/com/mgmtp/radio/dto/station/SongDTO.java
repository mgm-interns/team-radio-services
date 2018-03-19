package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.dto.user.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Data
public class SongDTO {
	String id;
	String songId;
	String source;
	boolean playing;
	boolean skipped;
	String url;
	String title;
	String thumbnail;
	int duration;
    UserDTO creator;
    int upVoteCount;
    int downVoteCount;
	List<UserDTO> upvoteUserList;
	List<UserDTO> downvoteUserList;
	String message;
	LocalDate createdAt;
}
