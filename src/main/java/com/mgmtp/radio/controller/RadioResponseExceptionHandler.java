package com.mgmtp.radio.controller;

import com.mgmtp.radio.controller.response.RadioErrorResponse;
import com.mgmtp.radio.domain.station.ActiveStation;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.exception.RadioServiceException;
import com.mgmtp.radio.mapper.user.UserMapper;
import com.mgmtp.radio.support.ActiveStationStore;
import com.mgmtp.radio.support.UserHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

@Log4j2
@ControllerAdvice
public class RadioResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;
    private final ActiveStationStore activeStationStore;
    private final UserHelper userHelper;
    private final UserMapper userMapper;

    public RadioResponseExceptionHandler(MessageSource messageSource, ActiveStationStore activeStationStore, UserHelper userHelper, UserMapper userMapper) {
        this.messageSource = messageSource;
        this.activeStationStore = activeStationStore;
        this.userHelper = userHelper;
        this.userMapper = userMapper;
    }

    // return 500 INTERNAL_SERVER_ERROR
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleNotFoundException(Exception exception, WebRequest webRequest) {
        log.error("Exception occurred when processing request", exception);
        return new ResponseEntity<>("Resource Not Found", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // return 400 BAD_REQUEST
    @ExceptionHandler(RadioBadRequestException.class)
    protected ResponseEntity<Object> handleException(RadioBadRequestException exception, WebRequest webRequest) {
        log.error("Exception processing request", exception);
        return new ResponseEntity<>(new RadioErrorResponse(exception.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // return 500 INTERNAL_SERVER_ERROR
    @ExceptionHandler(RadioServiceException.class)
    protected ResponseEntity<Object> handleException(RadioServiceException exception, WebRequest webRequest) {
        log.error("Exception processing request", exception);

        String message;

        if (StringUtils.isEmpty(exception.getMessage())) {
            message = "Radio service encountered an unexpected error";
        } else {
            message = exception.getMessage();
        }

        return new ResponseEntity<>(new RadioErrorResponse(message), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // return 404 NOT_FOUND
    @ExceptionHandler(RadioNotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(RadioNotFoundException exception, WebRequest webRequest) {
        log.error("Exception processing request", exception);

        String message;

        if (StringUtils.isEmpty(exception.getMessage())) {
            message = messageSource.getMessage("exception.not_found", new String[]{}, Locale.getDefault());
        } else {
            message = exception.getMessage();
        }
        return new ResponseEntity<>(new RadioErrorResponse(message), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    // return 202 ACCEPTED
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<Object> handleIOException(IOException exception, WebRequest webRequest, HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        String stationId = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        ActiveStation activeStation = activeStationStore.getActiveStations().get(stationId);

        Optional.ofNullable(activeStation).map(station -> {
            Optional<User> user = userHelper.getCurrentUser();
            if (user.isPresent()) {
                UserDTO userDTO = userMapper.userToUserDTO(user.get());
                activeStation.getUsers().remove(userDTO);
            }
            return station;
        });
        return new ResponseEntity<>(new RadioErrorResponse(exception.getMessage()), new HttpHeaders(), HttpStatus.ACCEPTED);
    }
}
