package com.mgmtp.radio.domain.station;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.mgmtp.radio.sdo.StationPrivacy;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "station")
@Data
public class Station {
	@Id
	private String id;
	@Indexed(unique = true)
	private String name;
	private StationPrivacy privacy;
	private String ownerId;
	private int startingTime;
	private boolean deleted;
	private List<String> playlist;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate createdAt;
	private StationConfiguration stationConfiguration;

	public Station() {
		this.stationConfiguration = new StationConfiguration();
	}
}
