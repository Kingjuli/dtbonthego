package com.dtbonthego.storeofvalueservice.exception;

/**
 * Exception thrown when a user attempts to access or modify a resource they don't own.
 * This exception is logged for security auditing purposes.
 */
public class SecurityViolationException extends RuntimeException {
    
    /**
     * Constructs a new SecurityViolationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public SecurityViolationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new SecurityViolationException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public SecurityViolationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new SecurityViolationException for unauthorized account access.
     * 
     * @param accountIdentifier the account identifier (ID or number)
     * @param userId the user ID attempting the access
     * @return a new SecurityViolationException
     */
    public static SecurityViolationException unauthorizedAccountAccess(String accountIdentifier, Long userId) {
        return new SecurityViolationException(
                String.format("Security alert: Unauthorized attempt by user ID '%d' to access account '%s'", 
                userId, accountIdentifier));
    }
    
    /**
     * Constructs a new SecurityViolationException for unauthorized status change.
     * 
     * @param accountIdentifier the account identifier
     * @param requestedStatus the requested status
     * @return a new SecurityViolationException
     */
    public static SecurityViolationException unauthorizedStatusChange(String accountIdentifier, String requestedStatus) {
        return new SecurityViolationException(
                String.format("Security alert: Unauthorized attempt to change account '%s' to status '%s'", 
                accountIdentifier, requestedStatus));
    }
    
    /**
     * Constructs a new SecurityViolationException for authentication failure.
     * 
     * @return a new SecurityViolationException
     */
    public static SecurityViolationException authenticationFailure() {
        return new SecurityViolationException(
                "Unable to authenticate user. Please check your credentials and try again.");
    }
}