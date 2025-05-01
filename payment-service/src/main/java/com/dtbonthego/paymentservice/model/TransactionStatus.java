package com.dtbonthego.paymentservice.model;

/**
 * Enum representing the status of a transaction.
 */
public enum TransactionStatus {
    /**
     * Transaction is pending processing
     */
    PENDING,
    
    /**
     * Transaction has been successfully completed
     */
    COMPLETED,
    
    /**
     * Transaction has failed for some reason
     */
    FAILED,
    
    /**
     * Transaction is being processed
     */
    PROCESSING,
    
    /**
     * Transaction has been cancelled
     */
    CANCELLED
} 