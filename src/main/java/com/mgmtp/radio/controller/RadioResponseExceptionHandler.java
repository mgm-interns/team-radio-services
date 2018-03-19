package com.mgmtp.radio.controller;

import com.mgmtp.radio.controller.response.RadioErrorResponse;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioServiceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@ControllerAdvice
public class RadioResponseExceptionHandler extends ResponseEntityExceptionHandler {

    // return 500 INTERNAL_SERVER_ERROR
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleNotFoundException(Exception exception, WebRequest webRequest){
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
}
