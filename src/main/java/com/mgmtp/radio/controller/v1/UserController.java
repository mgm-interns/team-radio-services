package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.station.StationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.sdo.CloudinaryDataKeys;
import com.mgmtp.radio.sdo.StationPrivacy;
import com.mgmtp.radio.service.user.UserService;
import com.mgmtp.radio.support.CloudinaryHelper;
import com.mgmtp.radio.support.ContentTypeValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/users";

    private final UserService userService;
    private final CloudinaryHelper cloudinaryHelper;
    private final ContentTypeValidator contentTypeValidator;

    public UserController(UserService userService,
                          CloudinaryHelper cloudinaryHelper,
                          ContentTypeValidator contentTypeValidator) {
        this.userService = userService;
        this.cloudinaryHelper = cloudinaryHelper;
        this.contentTypeValidator = contentTypeValidator;
    }

    @ApiOperation(
            value = "Get current user",
            notes = "Returns current user"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUser() throws RadioNotFoundException {
        log.info("GET /api/v1/users/me");

        if(getCurrentUser().isPresent()) {
            return userService.getUserById(getCurrentUser().get().getId());
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }

    @ApiOperation(
            value = "Get user info",
            notes = "Returns user info"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable(value = "userId") String userId) throws RadioNotFoundException {
        log.info("GET /api/v1/users/" + userId);

        Optional<UserDTO> user = Optional.ofNullable(userService.getUserById(userId));
        if(user.isPresent()) {
            return user.get();
        } else {
            throw new RadioNotFoundException("user is not found");
        }
    }

    @ApiOperation(
            value = "Get all stations of current user",
            notes = "Returns all stations of current user"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @GetMapping("/me/stations")
    @ResponseStatus(HttpStatus.OK)
    public Flux<StationDTO> getAllStationOfUser() throws RadioNotFoundException {
        log.info("GET /api/v1/users/me/stations");

        if(getCurrentUser().isPresent()) {
            return userService.getStationByUserId(getCurrentUser().get().getId());
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }

    @ApiOperation(
            value = "Register an user",
            notes = "Returns the new user"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in request parameters", response = RadioBadRequestException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO register(@Validated @RequestBody UserDTO userDTO,
                                                  BindingResult bindingResult)
            throws RadioException {
        log.info("POST /api/v1/users/register  - data: " + userDTO.toString());

        if (bindingResult.hasErrors()) {
            throw new RadioBadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        return userService.register(userDTO);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> patchUserPassword(@RequestBody Map<String, Object> body) throws RadioException {
        log.info("PATCH /api/v1/users/me/password - data: " + body.toString());

        String currentPassword = body.get("old_password").toString();
        String newPassword = body.get("new_password").toString();

        if(getCurrentUser().isPresent()) {
            boolean isChanged = false;

            if(!StringUtils.isEmpty(currentPassword) && !StringUtils.isEmpty(newPassword)) {
                isChanged = userService.changePassword(getCurrentUser().get().getId(), currentPassword, newPassword);
            }

            if(isChanged) {
                Map<String, Object> result = new HashMap<>();
                result.put("is_changed", isChanged);
                result.put("description", "Password is changed successfully.");
                return result;
            }

            throw new RadioBadRequestException("password is invalid");
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> forgotPassword(@RequestBody Map<String, Object> body) throws RadioException {
        log.info("POST /api/v1/users/forgot-password - data: " + body.toString());

        String email = body.get("email").toString();
        userService.forgotPassword(email);

        Map<String, Object> response = new HashMap<>();
        response.put("request_reset_password", true);
        response.put("description", "system has already sent reset password token.");
        return response;
    }

    @PostMapping("/reset-password/{resetPasswordToken}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> resetPassword(@PathVariable(value = "resetPasswordToken") String resetPasswordToken,
                                             @RequestBody Map<String, Object> body)
            throws RadioException {
        log.info("POST /api/v1/users/reset-password/" + resetPasswordToken);

        String password = body.get("password").toString();
        userService.resetPassword(resetPasswordToken, password);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("description", "your password reset.");
        return response;
    }

    @ApiOperation(
            value = "Patch the current user",
            notes = "Returns the updated user"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in user is not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO patchUser(@RequestBody UserDTO userDTO) throws RadioNotFoundException {
        log.info("PATCH /api/v1/users/me - data: " + userDTO.toString());

        if(getCurrentUser().isPresent()) {
            return userService.patchUser(getCurrentUser().get().getId(), userDTO);
        } else {
            throw new RadioNotFoundException("unauthorized");
        }
    }

    @ApiOperation(
            value = "Upload and patch current user avatar",
            notes = "Returns the updated user"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in user is not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PatchMapping("/me/avatar")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO patchUserAvatar(@RequestParam("file") MultipartFile multipartFile) throws RadioException, IOException {
        log.info("PATCH /api/v1/users/me/avatar");

        validateFileIsImage(multipartFile);

        Map<String, String> uploadResult = cloudinaryHelper.pushFileToCloud(multipartFile);

        if(!getCurrentUser().isPresent()) {
            throw new RadioNotFoundException("unauthorized");
        }

        if(uploadResult.get(CloudinaryDataKeys.secure_url.name()) != null) {
            User currentUser = getCurrentUser().get();
            final String avatarUrl = uploadResult.get(CloudinaryDataKeys.secure_url.name());
            return userService.patchUserAvatar(currentUser.getId(), avatarUrl);
        } else {
            throw new RadioException("Can not upload");
        }
    }

    @ApiOperation(
            value = "Upload and patch cover photo of current user",
            notes = "Returns the updated user"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in user is not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PatchMapping("/me/cover")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO patchCoverPhoto(@RequestParam("file") MultipartFile multipartFile) throws RadioException, IOException {
        log.info("PATCH /api/v1/users/me/cover");

        validateFileIsImage(multipartFile);

        if(!getCurrentUser().isPresent()) {
            throw new RadioNotFoundException("unauthorized");
        }

        Map<String, String> uploadResult = cloudinaryHelper.pushFileToCloud(multipartFile);
        if(uploadResult.get(CloudinaryDataKeys.secure_url.name()) != null) {
            User currentUser = getCurrentUser().get();
            final String coverUrl = uploadResult.get(CloudinaryDataKeys.secure_url.name());
            return userService.patchUserCover(currentUser.getId(), coverUrl);
        } else {
            throw new RadioException("Can not upload");
        }
    }

    @ApiOperation(
            value = "Get public station of a specific user",
            notes = "Returns the updated user"
    )

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in user is not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })

    @GetMapping("/{userId}/stations")
    @ResponseStatus(HttpStatus.OK)
    public Flux<StationDTO> getListStationOfUser(@PathVariable(value = "userId") String userId) throws RadioNotFoundException {
        log.info("GET /api/v1/users/" + userId + "/stations");

        Optional<UserDTO> user = Optional.ofNullable(userService.getUserById(userId));
        if(user.isPresent()) {
            return userService.getStationsByUserIdAndPrivacy(userId, StationPrivacy.station_public);
        } else {
            throw new RadioNotFoundException("user is not found");
        }
    }

    void validateFileIsImage( MultipartFile multipartFile) throws RadioBadRequestException{
        if (multipartFile == null
                || multipartFile.isEmpty()
                || !contentTypeValidator.isImage(multipartFile)) {
            throw new RadioBadRequestException("Bad request");
        }
    }
}
