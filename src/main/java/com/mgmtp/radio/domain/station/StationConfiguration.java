package com.mgmtp.radio.domain.station;

import com.mgmtp.radio.dto.station.SkipRuleDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

public class StationConfiguration {
	@Document(collection = "stationConfiguration")
	@Data
	@NoArgsConstructor
	public class Station {
		@Id
		private String stationId;
		@Indexed(unique = true)
		private SkipRuleDTO skipRuleDTO;

		public SkipRuleDTO getSkipRuleDTO() {
			return this.skipRuleDTO;
		}

	}

}
