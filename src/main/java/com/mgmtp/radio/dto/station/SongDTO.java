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
	String youtubeId;
	boolean playing;
	boolean skipped;
	String url;
	String title;
	String thumbnail;
	int duration;
	String creatorId;
	List<UserDTO> upVoteUserList;
	List<UserDTO> downVoteUserList;
	String message;
	LocalDate createdAt;
}
