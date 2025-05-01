package com.dtbonthego.eventsservice.exception;

import com.dtbonthego.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Global exception handler for the Events Service.
 * Extends the common GlobalExceptionHandler to handle service-specific exceptions.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends com.dtbonthego.common.exception.GlobalExceptionHandler {

    /**
     * Handles service-specific exceptions.
     *
     * @param ex the exception
     * @param request the web request
     * @return the error response
     */
    @ExceptionHandler(ServiceSpecificException.class)
    public ResponseEntity<ErrorResponse> handleServiceSpecificException(
            ServiceSpecificException ex, WebRequest request) {
        log.error("Service-specific error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
