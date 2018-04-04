package com.mgmtp.radio.domain.station;

import com.mgmtp.radio.dto.station.SkipRuleDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Data
@Document(collection = "stationConfiguration")
public class StationConfiguration {
	@Id
	private String Id;
	private SkipRule skipRule;
}
