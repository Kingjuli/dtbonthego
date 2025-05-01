package com.dtbonthego.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is an error processing a transaction.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TransactionProcessingException extends RuntimeException {
    
    public TransactionProcessingException(String message) {
        super(message);
    }
    
    public TransactionProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static TransactionProcessingException databaseError(String operation) {
        return new TransactionProcessingException(
                String.format("Database error occurred during %s operation", operation));
    }
    
    public static TransactionProcessingException timeoutError() {
        return new TransactionProcessingException(
                "Transaction processing timed out");
    }
} 