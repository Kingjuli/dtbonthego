package com.dtbonthego.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an account could not be found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {
    
    public AccountNotFoundException(String accountNumber) {
        super(String.format("Account with number %s not found", accountNumber));
    }
} 