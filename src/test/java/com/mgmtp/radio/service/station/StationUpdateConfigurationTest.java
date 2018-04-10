package com.mgmtp.radio.service.station;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.controller.v1.StationController;
import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.skipRule.SkipRuleDTO;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.service.station.StationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class StationUpdateConfigurationTest {

	WebTestClient webTestClient;

	StationController stationController;

	@Mock
	StationService stationService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(stationService);
		stationController = new StationController(stationService);
		webTestClient = WebTestClient.bindToController(stationController).build();
	}

	@Test
	public void updateStationSkipRuleToAdvance() {
		final String id= "123456";

		StationConfigurationDTO stationConfigurationDTO = new StationConfigurationDTO();
		stationConfigurationDTO.setSkipRule(new SkipRuleDTO(SkipRule.ADVANCE));
		Mono<StationConfigurationDTO> dto = stationService.updateConfiguration(id, stationConfigurationDTO);
		System.out.println(dto == null);
		dto.subscribe(dto1-> {
			assert (dto1.getSkipRule().getTypeId() == stationConfigurationDTO.getSkipRule().getTypeId());
		});
	}
}
