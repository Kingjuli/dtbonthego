package com.dtbonthego.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 * This is a common exception used across all microservices.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new resource not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new resource not found exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new ResourceNotFoundException with a standard message for account not found.
     *
     * @param identifier the account identifier (ID or account number)
     * @return a new ResourceNotFoundException
     */
    public static ResourceNotFoundException accountNotFound(String identifier) {
        return new ResourceNotFoundException(
                String.format("Account not found with identifier: %s", identifier));
    }
    
    /**
     * Constructs a new ResourceNotFoundException with a standard message for profile not found.
     *
     * @param profileId the profile ID
     * @return a new ResourceNotFoundException
     */
    public static ResourceNotFoundException profileNotFound(Long profileId) {
        return new ResourceNotFoundException(
                String.format("Profile not found with ID: %d", profileId));
    }
    
    /**
     * Constructs a new ResourceNotFoundException with a standard message for notification not found.
     *
     * @param id the notification ID
     * @return a new ResourceNotFoundException
     */
    public static ResourceNotFoundException notificationNotFound(Long id) {
        return new ResourceNotFoundException(
                String.format("Notification not found with ID: %d", id));
    }
    
    /**
     * Constructs a new ResourceNotFoundException with a standard message for transaction not found.
     *
     * @param id the transaction ID
     * @return a new ResourceNotFoundException
     */
    public static ResourceNotFoundException transactionNotFound(Long id) {
        return new ResourceNotFoundException(
                String.format("Transaction not found with ID: %d", id));
    }
}
