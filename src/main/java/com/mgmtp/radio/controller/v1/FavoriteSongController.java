package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.support.validator.user.CreateFavoriteSongValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping(FavoriteSongController.BASE_URL)
public class FavoriteSongController {

	public static final String BASE_URL = "/api/v1/users/{id}/favorites";

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

	@PostMapping
	public Mono<ResponseEntity<FavoriteSongDTO>> create(@PathVariable(value = "id") String userId, @Validated @RequestBody FavoriteSongDTO favoriteSongDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return Mono.error(new RadioBadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage()));
		}
		return favoriteSongService.create(userId, favoriteSongDTO).map(song -> ResponseEntity.status(HttpStatus.CREATED).body(song));
	}
}
