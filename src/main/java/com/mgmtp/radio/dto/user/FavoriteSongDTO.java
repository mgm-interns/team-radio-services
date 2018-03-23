package com.mgmtp.radio.dto.user;

import com.mgmtp.radio.dto.station.SongDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@NoArgsConstructor
@Data
public class FavoriteSongDTO {
	String id;
	String userId;
	@NotEmpty
	String songId;
	SongDTO song;
	LocalDate createdAt;
}
