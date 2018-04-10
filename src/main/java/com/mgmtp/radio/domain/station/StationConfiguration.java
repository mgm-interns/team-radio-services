package com.mgmtp.radio.domain.station;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Data
@Document(collection = "stationConfiguration")
public class StationConfiguration {
	private SkipRule rule;}
