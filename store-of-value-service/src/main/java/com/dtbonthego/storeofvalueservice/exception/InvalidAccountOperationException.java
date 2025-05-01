package com.dtbonthego.storeofvalueservice.exception;

/**
 * Exception thrown when an invalid operation is attempted on an account.
 */
public class InvalidAccountOperationException extends RuntimeException {
    
    /**
     * Constructs a new InvalidAccountOperationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public InvalidAccountOperationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new InvalidAccountOperationException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidAccountOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new InvalidAccountOperationException for an invalid account status transition.
     * 
     * @param currentStatus the current status
     * @param requestedStatus the requested status
     * @return a new InvalidAccountOperationException
     */
    public static InvalidAccountOperationException invalidStatusTransition(String currentStatus, String requestedStatus) {
        return new InvalidAccountOperationException(
                String.format("Invalid account status transition from '%s' to '%s'", currentStatus, requestedStatus));
    }
    
    /**
     * Constructs a new InvalidAccountOperationException for operations on a closed account.
     * 
     * @param accountIdentifier the account identifier
     * @param operation the attempted operation
     * @return a new InvalidAccountOperationException
     */
    public static InvalidAccountOperationException operationOnClosedAccount(String accountIdentifier, String operation) {
        return new InvalidAccountOperationException(
                String.format("Operation '%s' cannot be performed on closed account '%s'", operation, accountIdentifier));
    }
    
    /**
     * Constructs a new InvalidAccountOperationException for operations on a suspended account.
     * 
     * @param accountIdentifier the account identifier
     * @param operation the attempted operation
     * @return a new InvalidAccountOperationException
     */
    public static InvalidAccountOperationException operationOnSuspendedAccount(String accountIdentifier, String operation) {
        return new InvalidAccountOperationException(
                String.format("Operation '%s' cannot be performed on suspended account '%s'", operation, accountIdentifier));
    }
}