package com.mgmtp.radio;

import com.mgmtp.radio.mapper.station.SongMapper;
import com.mgmtp.radio.mapper.station.SongMapperImpl;
import com.mgmtp.radio.mapper.station.SongMapperImpl_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

@Configuration
public class RadioApplicationTests {

	@Bean(name = "delegate")
	public SongMapper delegate(){
	    return new SongMapperImpl_();
    }

    @Bean(name = "songMapperImpl")
    public SongMapper songMapperImpl(){
	    return new SongMapperImpl();
    }

}
