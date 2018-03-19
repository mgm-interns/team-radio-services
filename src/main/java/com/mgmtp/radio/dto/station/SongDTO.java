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
	boolean isPlayed;
	boolean isSkipped;
	String url;
	String title;
	String thumbnail;
	int duration;
	String creatorId;
	List<UserDTO> upvoteUserList;
	List<UserDTO> downvoteUserList;
	String message;
	LocalDate createdAt;
}
