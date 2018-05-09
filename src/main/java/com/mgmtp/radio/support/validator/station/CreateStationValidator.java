package com.mgmtp.radio.support.validator.station;

import com.mgmtp.radio.dto.station.StationConfigurationDTO;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.service.station.StationService;
import com.mgmtp.radio.support.UserHelper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateStationValidator implements Validator {
	private final StationService stationService;
	private final MessageSourceAccessor messageSourceAccessor;
	private final UserHelper userHelper;

	public CreateStationValidator(StationService stationService, MessageSourceAccessor messageSourceAccessor, UserHelper userHelper) {
		this.stationService = stationService;
		this.messageSourceAccessor = messageSourceAccessor;
		this.userHelper = userHelper;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return StationDTO.class.isAssignableFrom(aClass) || StationConfigurationDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		StationDTO stationDTO = (StationDTO) target;

		this.validateStationName(stationDTO, errors);
		this.validateUnique(stationDTO, errors);
	}

	private void validateUnique(StationDTO stationDTO, Errors errors) {
		if (isStationExisted(stationDTO.getName())) {
			errors.rejectValue("id", "", messageSourceAccessor.getMessage("validation.error.unique", new String[]{"id"}));
		}
	}

	private void validateStationName(StationDTO stationDTO, Errors errors) {
		if (stationDTO.getName().trim().isEmpty()) {
			errors.rejectValue("name", "", messageSourceAccessor.getMessage("validation.error.empty", new String[]{"name"}));
		}
	}

	private boolean isStationExisted(String name) {
		return stationService.existsByName(name);
	}

}
