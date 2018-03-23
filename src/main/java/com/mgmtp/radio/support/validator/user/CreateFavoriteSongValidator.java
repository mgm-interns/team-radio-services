package com.mgmtp.radio.support.validator.user;

import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.service.user.UserService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateFavoriteSongValidator implements Validator {

	private final FavoriteSongService favoriteSongService;
	private final UserService userService;
	private final Environment env;

	public CreateFavoriteSongValidator(FavoriteSongService favoriteSongService, UserService userService, Environment env) {
		this.favoriteSongService = favoriteSongService;
		this.userService = userService;
		this.env = env;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return FavoriteSongDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		FavoriteSongDTO favoriteSongDTO = (FavoriteSongDTO) target;
		this.validateExists(favoriteSongDTO, errors);
		this.validateUnique(favoriteSongDTO, errors);
	}

	/**
	 * Validate exist logic
	 *
	 */
	private void validateExists(FavoriteSongDTO favoriteSongDTO, Errors errors) {
		if (!isUserExisted(favoriteSongDTO.getUserId())) {
			errors.rejectValue("userId", "", env.getProperty("user.favorite.error.exist.userId"));
		}

		if (!isSongExisted(favoriteSongDTO.getSongId())) {
			errors.rejectValue("songId", "", env.getProperty("user.favorite.error.exist.songId"));
		}
	}

	/**
	 * Validate unique logic
	 *
	 */
	private void validateUnique(FavoriteSongDTO favoriteSongDTO, Errors errors) {
		if (isFavoriteSongExisted(favoriteSongDTO.getUserId(), favoriteSongDTO.getSongId())) {
			errors.rejectValue("id", "", env.getProperty("user.favorite.error.exist.song"));
		}
	}

	/**
	 * Check if user exists or not
	 *
	 */
	private boolean isUserExisted(String userId) {
		try {
			userService.getUserById(userId);
			return true;
		} catch (RadioNotFoundException exception) {
			return false;
		}
	}

	/**
	 * Check if song exists or not
	 *
	 */
	private boolean isSongExisted(String songId) {
//		Todo: check song id
		return true;
	}

	/**
	 * Check if favorite song exists or not
	 *
	 */
	private boolean isFavoriteSongExisted(String userId, String songId) {
		try {
			favoriteSongService.findByUserIdAndSongId(userId, songId);
			return true;
		} catch (RadioNotFoundException exception) {
			return false;
		}
	}

}
