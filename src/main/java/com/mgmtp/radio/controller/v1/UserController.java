package com.mgmtp.radio.controller.v1;

import com.mgmtp.radio.controller.BaseRadioController;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController extends BaseRadioController {

    public static final String BASE_URL = "/api/v1/users";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    public RadioSuccessResponse<UserDTO> register(@RequestBody UserDTO userDTO) {
        UserDTO newUserDTO = userService.register(userDTO);
        return new RadioSuccessResponse<>(newUserDTO);
    }

    @ApiOperation(
            value = "Patch an user",
            notes = "Returns the updated user"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed successfully", response = RadioSuccessResponse.class),
            @ApiResponse(code = 400, message = "Error in user is not found", response = RadioNotFoundException.class),
            @ApiResponse(code = 500, message = "Server error", response = RadioException.class)
    })
    @PatchMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public RadioSuccessResponse<UserDTO> patchUser(@RequestBody UserDTO userDTO) throws RadioNotFoundException{
        User currentUser = getCurrentUser();
        UserDTO updatedUserDTO = userService.patchUser(currentUser.getUsername(), userDTO);
        return new RadioSuccessResponse<>(updatedUserDTO);
    }


}
