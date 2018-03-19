package com.mgmtp.radio.dto.station;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class StationDTO {
	String id;
	String name;
	boolean isPrivate;
	String ownerId;
	int startingTime;
	boolean isDeleted;
	List<SongDTO> playlist;
	LocalDate createdAt;
}
