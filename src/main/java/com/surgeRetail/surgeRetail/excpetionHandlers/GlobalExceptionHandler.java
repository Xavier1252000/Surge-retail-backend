package com.surgeRetail.surgeRetail.excpetionHandlers;

import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponseHandler> handleNumberFormatException(NumberFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseHandler(ex.getMessage(), null, ResponseStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), true));
    }

    @ExceptionHandler(CustomExceptions.class)
    public ResponseEntity<ApiResponseHandler> handleCustomExceptions(CustomExceptions ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseHandler(ex.getMessage(), null, ResponseStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), true));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseHandler> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseHandler("Incorrect password", null, ResponseStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), true));
    }
}
