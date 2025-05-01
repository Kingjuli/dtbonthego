package com.dtbonthego.paymentservice.model;

/**
 * Enum representing the types of transactions supported by the system.
 */
public enum TransactionType {
    /**
     * Deposit (top-up) of funds into an account
     */
    DEPOSIT,
    
    /**
     * Withdrawal of funds from an account
     */
    WITHDRAWAL,
    
    /**
     * Transfer of funds between two accounts
     */
    TRANSFER
} 