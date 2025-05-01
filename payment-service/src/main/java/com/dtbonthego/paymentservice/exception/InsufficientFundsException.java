package com.dtbonthego.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there are insufficient funds in an account for a requested operation.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(String accountNumber) {
        super(String.format("Insufficient funds in account %s", accountNumber));
    }
    
    public InsufficientFundsException(String accountNumber, String message) {
        super(String.format("Insufficient funds in account %s: %s", accountNumber, message));
    }
} 