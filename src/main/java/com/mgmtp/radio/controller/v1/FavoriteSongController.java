package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.support.validator.user.CreateFavoriteSongValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping(FavoriteSongController.BASE_URL)
public class FavoriteSongController extends BaseRadioController {

	public static final String BASE_URL = "/api/v1/users/{userId}/favorites";

	private final FavoriteSongService favoriteSongService;
	private final CreateFavoriteSongValidator createFavoriteSongValidator;

	public FavoriteSongController(FavoriteSongService favoriteSongService, CreateFavoriteSongValidator createFavoriteSongValidator) {
		this.favoriteSongService = favoriteSongService;
		this.createFavoriteSongValidator = createFavoriteSongValidator;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.addValidators(this.createFavoriteSongValidator);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<RadioSuccessResponse<FavoriteSongDTO>> index(@PathVariable(value = "userId") String userId) {
		// Todo: validate logged in user
		return favoriteSongService.findByUserId(userId).map(favoriteSong -> new RadioSuccessResponse<>(favoriteSong));
	}


	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public Mono<RadioSuccessResponse<FavoriteSongDTO>> create(@PathVariable(value = "userId") String userId, @Validated @RequestBody FavoriteSongDTO favoriteSongDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return Mono.error(new RadioBadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage()));
		}
		// Todo: validate logged in user
		return favoriteSongService.create(userId, favoriteSongDTO).map(song -> new RadioSuccessResponse<>(song));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<RadioSuccessResponse<Void>> delete(@PathVariable(value = "id") String favoriteSongId) {
		// Todo: after getting current user: String userId = getCurrentUser().getId();
		final String userId = "001";
		return favoriteSongService.delete(favoriteSongId, userId).map(song -> new RadioSuccessResponse<>());
	}
}
