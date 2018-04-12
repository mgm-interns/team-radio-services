package com.mgmtp.radio.domain.station;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.mgmtp.radio.sdo.StationPrivacy;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Document(collection = "station")
@Data
public class Station {
	@Id
	private String id;

	@Indexed(unique = true)
	private String name;

	private String friendlyId;

	private StationPrivacy privacy = StationPrivacy.station_public;

	private String ownerId;

	private List<String> playlist = Collections.emptyList();

	private List<String> history = Collections.emptyList();

	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate createdAt;
	private StationConfiguration stationConfiguration;

	public Station() {
		this.stationConfiguration = new StationConfiguration();
	}
}
