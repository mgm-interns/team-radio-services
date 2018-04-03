package com.mgmtp.radio.support.validator.user;

import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.service.user.UserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Component
public class CreateFavoriteSongValidator implements Validator {

	private final FavoriteSongService favoriteSongService;
	private final UserService userService;
	private final MessageSource messageSource;
	private final SongService songService;

	public CreateFavoriteSongValidator(FavoriteSongService favoriteSongService, UserService userService, MessageSource messageSource, SongService songService) {
		this.favoriteSongService = favoriteSongService;
		this.userService = userService;
		this.messageSource = messageSource;
		this.songService = songService;
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

	private void validateExists(FavoriteSongDTO favoriteSongDTO, Errors errors) {
		if (!isUserExisted(favoriteSongDTO.getUserId())) {
			errors.rejectValue("userId", "", messageSource.getMessage("validation.error.exist", new String[]{"User Id"}, Locale.getDefault()));
		}

		if (!isSongExisted(favoriteSongDTO.getSongId())) {
			errors.rejectValue("songId", "", messageSource.getMessage("validation.error.exist", new String[]{"Song Id"}, Locale.getDefault()));
		}
	}

	private void validateUnique(FavoriteSongDTO favoriteSongDTO, Errors errors) {
		if (isFavoriteSongExisted(favoriteSongDTO.getUserId(), favoriteSongDTO.getSongId())) {
			errors.rejectValue("id", "", messageSource.getMessage("validation.error.unique", new String[]{"Favorite Song"}, Locale.getDefault()));
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
		return songService.existsById(songId).block();
	}

	private boolean isFavoriteSongExisted(String userId, String songId) {
	    return favoriteSongService.existsByUserIdAndSongId(userId, songId).block();
	}

}
