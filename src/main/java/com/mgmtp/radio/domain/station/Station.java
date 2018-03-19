package com.mgmtp.radio.domain.station;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "station")
@Data
@Builder
@NoArgsConstructor
public class Station {

}
