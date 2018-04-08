package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.RadioApplication;
import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.dto.station.SkipRuleDTO;
import com.mgmtp.radio.dto.station.SkipRuleDTO.InvalidRuleTypeDtoException;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.service.station.StationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

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
		StationConfigurationDTO stationConfigurationDTO = new StationConfigurationDTO();
		try {
			stationConfigurationDTO.setSkipRule(new SkipRuleDTO(SkipRule.ADVANCE));
			stationService.updateConfiguration(stationConfigurationDTO);
		} catch (InvalidRuleTypeDtoException e) {
			e.printStackTrace();
		}
	}
}
