package com.dtbonthego.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transaction could not be found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {
    
    public TransactionNotFoundException(String referenceNumber) {
        super(String.format("Transaction with reference number %s not found", referenceNumber));
    }
    
    public TransactionNotFoundException(Long id) {
        super(String.format("Transaction with ID %d not found", id));
    }
} 