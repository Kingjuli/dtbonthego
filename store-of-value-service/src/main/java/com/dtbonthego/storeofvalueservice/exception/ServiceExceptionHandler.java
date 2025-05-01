package com.dtbonthego.storeofvalueservice.exception;

import com.dtbonthego.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Exception handler for the Store of Value Service.
 * Extends the common GlobalExceptionHandler to handle service-specific exceptions.
 */
@RestControllerAdvice
@Slf4j
public class ServiceExceptionHandler extends com.dtbonthego.common.exception.GlobalExceptionHandler {

    /**
     * Handles InvalidAccountOperationException.
     *
     * @param ex the exception
     * @param request the web request
     * @return the error response
     */
    @ExceptionHandler(InvalidAccountOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountOperationException(
            InvalidAccountOperationException ex, WebRequest request) {
        log.error("Invalid account operation: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles SecurityViolationException.
     *
     * @param ex the exception
     * @param request the web request
     * @return the error response
     */
    @ExceptionHandler(SecurityViolationException.class)
    public ResponseEntity<ErrorResponse> handleSecurityViolationException(
            SecurityViolationException ex, WebRequest request) {
        log.error("Security violation: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now());
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
