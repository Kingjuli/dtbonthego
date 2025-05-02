package com.dtbonthego.paymentservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a transaction event to be published to Kafka.
 * This is the payload that will be sent to the Events Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    
    /**
     * The ID of the transaction
     */
    private Long transactionId;
    
    /**
     * The ID of the profile/user who initiated the transaction
     */
    private Long profileId;
    
    /**
     * The account number from which the transaction was made
     */
    private String sourceAccountNumber;
    
    /**
     * The account number to which the transaction was made (if applicable)
     */
    private String destinationAccountNumber;
    
    /**
     * The amount of the transaction
     */
    private BigDecimal amount;
    
    /**
     * The type of transaction (DEPOSIT, WITHDRAWAL, TRANSFER)
     */
    private String transactionType;
    
    /**
     * The status of the transaction (COMPLETED, FAILED, PENDING)
     */
    private String status;
    
    /**
     * The timestamp when the transaction occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * Optional description or reference for the transaction
     */
    private String description;
    
    /**
     * The email address of the customer (for notifications)
     */
    private String customerEmail;
    
    /**
     * The phone number of the customer (for notifications)
     */
    private String customerPhoneNumber;
    
    /**
     * The name of the customer (for personalized notifications)
     */
    private String customerName;
} 