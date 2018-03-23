package com.mgmtp.radio.support.validator.user;

import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.service.user.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateFavoriteSongValidator implements Validator {

	private final FavoriteSongService favoriteSongService;
	private final UserService userService;

	public CreateFavoriteSongValidator(FavoriteSongService favoriteSongService, UserService userService) {
		this.favoriteSongService = favoriteSongService;
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return FavoriteSongDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		FavoriteSongDTO favoriteSongDTO = (FavoriteSongDTO) target;
		this.validateExists(favoriteSongDTO, errors);
	}

	private void validateExists(FavoriteSongDTO favoriteSongDTO, Errors errors) {
		if (!isUserExisted(favoriteSongDTO.getUserId())) {
			errors.rejectValue("userId", "", "User id does not exist");
		}

		if (!isSongExisted(favoriteSongDTO.getSongId())) {
			errors.rejectValue("songId", "", "Song id does not exist");
		}
	}
	private boolean isUserExisted(String userId) {
		try {
			userService.getUserById(userId);
			return true;
		} catch (RadioNotFoundException exception) {
			return false;
		}
	}

	private boolean isSongExisted(String songId) {
//		Todo: check song id
		return true;
	}
}
