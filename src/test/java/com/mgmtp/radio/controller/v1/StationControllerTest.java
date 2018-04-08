package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.domain.station.SkipRule;
import com.mgmtp.radio.dto.station.SkipRuleDTO;
import com.mgmtp.radio.dto.station.SkipRuleDTO.InvalidRuleTypeDtoException;
import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class StationControllerTest {

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
	public void getAllStation() {
	}

	@Test
	public void getStation() {
	}

	@Test
	public void createStation() throws InvalidRuleTypeDtoException {
		StationDTO stationDTO = new StationDTO();
		stationDTO.setId("RRRE");
		Mono<StationDTO> stationDTOMono = stationController.createStation(stationDTO);
		assertEquals(stationDTO.getId(),stationDTOMono.block().getId());
	}

	@Test
	public void updateStation() {
	}

	@Test
	public void updateStationSkipRuleToBasic() {
		StationConfigurationDTO stationConfigurationDTO = new StationConfigurationDTO();
		try {
			stationConfigurationDTO.setId("1112");
			stationConfigurationDTO.setSkipRule(new SkipRuleDTO(SkipRule.BASIC));
			Mono<StationConfigurationDTO> mnStation = stationService.updateConfiguration(stationConfigurationDTO);
			StationConfigurationDTO dto = mnStation.block();
			assertEquals(dto.getId(), stationConfigurationDTO.getId());
		} catch (InvalidRuleTypeDtoException e) {
			e.printStackTrace();
		}
	}
}