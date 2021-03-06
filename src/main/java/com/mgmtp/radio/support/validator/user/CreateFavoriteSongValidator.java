package com.mgmtp.radio.support.validator.user;

import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.service.station.SongService;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.support.UserHelper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateFavoriteSongValidator implements Validator {

	private final FavoriteSongService favoriteSongService;
	private final SongService songService;
	private final MessageSourceAccessor messageSourceAccessor;
	private final UserHelper userHelper;

	public CreateFavoriteSongValidator(FavoriteSongService favoriteSongService, SongService songService, MessageSourceAccessor messageSourceAccessor, UserHelper userHelper) {
		this.favoriteSongService = favoriteSongService;
		this.songService = songService;
		this.messageSourceAccessor = messageSourceAccessor;
		this.userHelper = userHelper;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return FavoriteSongDTO.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		FavoriteSongDTO favoriteSongDTO = (FavoriteSongDTO) target;
		if (userHelper.getCurrentUser().isPresent()) {
			favoriteSongDTO.setUserId(userHelper.getCurrentUser().get().getId());
		} else {
			errors.rejectValue("userId", "", messageSourceAccessor.getMessage("exception.not_found"));
			return;
		}

		this.validateExists(favoriteSongDTO, errors);
		this.validateUnique(favoriteSongDTO, errors);
	}

	private void validateExists(FavoriteSongDTO favoriteSongDTO, Errors errors) {
		if (!isSongExisted(favoriteSongDTO.getSongId())) {
			errors.rejectValue("songId", "", messageSourceAccessor.getMessage("validation.error.exist", new String[]{"songId: " + favoriteSongDTO.getSongId()}));
		}
	}

	private void validateUnique(FavoriteSongDTO favoriteSongDTO, Errors errors) {
		if (isFavoriteSongExisted(favoriteSongDTO.getUserId(), favoriteSongDTO.getSongId())) {
			errors.rejectValue("id", "", messageSourceAccessor.getMessage("validation.error.unique", new String[]{"id"}));
		}
	}

	private boolean isSongExisted(String songId) {
		return songService.existsBySongId(songId);
	}

	private boolean isFavoriteSongExisted(String userId, String songId) {
		return favoriteSongService.existsByUserIdAndSongId(userId, songId);
	}

}
