package com.mgmtp.radio.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class Constant {

    @Value("${event.join_station}")
    private String event_join_station;
}
