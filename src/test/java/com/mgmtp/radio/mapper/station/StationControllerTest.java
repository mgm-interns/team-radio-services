package com.mgmtp.radio.mapper.station;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.controller.v1.StationController;
import com.mgmtp.radio.domain.station.Station;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.respository.station.StationRepository;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.service.station.StationServiceImpl;
import com.mgmtp.radio.support.validator.station.CreateStationValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.BindingResult;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class StationControllerTest {

	WebTestClient webTestClient;

	StationController stationController;

	StationService stationService;

	StationMapper stationMapper;

	@Mock
	StationRepository stationRepository;

	@Mock
	BindingResult bindingResult;

	CreateStationValidator createStationValidator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		stationService = new StationServiceImpl(stationMapper, stationRepository, null);
		createStationValidator = new CreateStationValidator(stationService,null,null);
		stationController = new StationController(stationService,null,null, createStationValidator);
		webTestClient = WebTestClient.bindToController(stationController).build();

	}

	@Test
	public void createDuplicateStationTest() {

		String stationId = "station01";
		String stationName = "Station 01";

		StationDTO stationDTO = new StationDTO();
		stationDTO.setId(stationId);
		stationDTO.setName(stationName);

		Station station = new Station();
		station.setId(stationName);

		when(stationRepository.save(station)).thenReturn(Mono.just(station));

		when(stationRepository.findById(stationId)).thenReturn(Mono.just(station));

		when(bindingResult.hasErrors()).thenReturn(true);

		boolean isCreatingBlockedDueToDuplicateName = false;

		try {
			Mono<StationDTO> stationDTOMono = stationController.createStation(stationDTO, bindingResult);
		} catch (RadioException e) {
			isCreatingBlockedDueToDuplicateName = true;
			e.printStackTrace();
		}
	}
}