package com.dtbonthego.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error response model specifically for validation errors.
 * Extends the standard ErrorResponse with field-specific validation errors.
 */
@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    
    private Map<String, String> errors;
    
    /**
     * Constructs a new validation error response.
     *
     * @param status the HTTP status code
     * @param message the error message
     * @param path the request path
     * @param timestamp the timestamp when the error occurred
     * @param errors the field-specific validation errors
     */
    public ValidationErrorResponse(int status, String message, String path, LocalDateTime timestamp, Map<String, String> errors) {
        super(status, message, path, timestamp);
        this.errors = errors;
    }
}
