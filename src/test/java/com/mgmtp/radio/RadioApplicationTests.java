package com.mgmtp.radio;

import com.mgmtp.radio.config.RadioConfig;
import com.mgmtp.radio.config.YouTubeConfig;
import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.station.SongMapperImpl;
import com.mgmtp.radio.mapper.station.SongMapperImpl_;
import com.mgmtp.radio.support.StationPlayerHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {YouTubeConfig.class})
public class RadioApplicationTests {

	@Bean(name = "delegate")
	public SongMapper delegate(){
	    return new SongMapperImpl_();
    }

    @Bean(name = "songMapperImpl")
    public SongMapper songMapperImpl(){
	    return new SongMapperImpl();
    }

    @Bean(name = "stationPlayerHelper")
    public StationPlayerHelper stationPlayerHelper() {
        return new StationPlayerHelper();
    }
}
