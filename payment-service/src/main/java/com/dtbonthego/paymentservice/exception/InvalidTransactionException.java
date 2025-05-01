package com.dtbonthego.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transaction is invalid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTransactionException extends RuntimeException {
    
    public InvalidTransactionException(String message) {
        super(message);
    }
    
    public static InvalidTransactionException sameSourceAndDestination() {
        return new InvalidTransactionException("Source and destination accounts cannot be the same");
    }
    
    public static InvalidTransactionException invalidAmount() {
        return new InvalidTransactionException("Transaction amount must be greater than zero");
    }
    
    public static InvalidTransactionException accountNotActive(String accountNumber) {
        return new InvalidTransactionException(String.format("Account %s is not active", accountNumber));
    }
    
    public static InvalidTransactionException concurrentModification() {
        return new InvalidTransactionException("Transaction could not be processed due to concurrent modification");
    }
} 