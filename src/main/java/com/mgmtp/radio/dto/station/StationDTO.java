package com.mgmtp.radio.dto.station;

import com.mgmtp.radio.sdo.StationPrivacy;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class StationDTO {
	String id;
	String name;
	StationPrivacy privacy;
	String ownerId;
	int startingTime;
	boolean deleted;
	List<SongDTO> playlist;
	LocalDate createdAt;
}
