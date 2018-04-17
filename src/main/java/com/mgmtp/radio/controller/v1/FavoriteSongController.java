package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.dto.user.FavoriteSongDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.user.FavoriteSongService;
import com.mgmtp.radio.support.validator.user.CreateFavoriteSongValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	public static final String BASE_URL = "/api/v1/users/me/favorites";

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
	public Flux<FavoriteSongDTO> getAll() throws RadioException {

		if(getCurrentUser().isPresent()) {
		    String userId = getCurrentUser().get().getId();
			return favoriteSongService.findByUserId(userId);
		} else {
			throw new RadioNotFoundException("unauthorized");
		}
	}


	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public Mono<FavoriteSongDTO> store(@Validated @RequestBody FavoriteSongDTO favoriteSongDTO,
                                                             BindingResult bindingResult)
            throws RadioException {
		if (bindingResult.hasErrors()) {
			return Mono.error(new RadioBadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage()));
		}

        if(getCurrentUser().isPresent()) {
            String userId = getCurrentUser().get().getId();
            return favoriteSongService.create(userId, favoriteSongDTO);
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
	}

	@DeleteMapping("/{songId}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ResponseEntity> delete(@PathVariable(value = "songId") String songId) throws RadioException {
        if (getCurrentUser().isPresent()) {
            String userId = getCurrentUser().get().getId();
            return favoriteSongService.delete(songId, userId).map(result -> {
            	if (Long.compare(result, 0L) != 0) {
            		return ResponseEntity.ok().build();
				}
				return ResponseEntity.notFound().build();
			});
        } else {
            throw new RadioNotFoundException("unauthorized");
        }

	}
}
