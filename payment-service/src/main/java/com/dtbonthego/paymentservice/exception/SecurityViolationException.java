package com.dtbonthego.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a security violation is detected.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class SecurityViolationException extends RuntimeException {
    
    public SecurityViolationException(String message) {
        super(message);
    }
    
    public static SecurityViolationException unauthorizedAccountAccess(String accountNumber, Long profileId) {
        return new SecurityViolationException(
                String.format("User with profile ID %d is not authorized to access account %s", 
                        profileId, accountNumber));
    }
    
    public static SecurityViolationException authenticationFailure() {
        return new SecurityViolationException("Authentication failure");
    }
} 