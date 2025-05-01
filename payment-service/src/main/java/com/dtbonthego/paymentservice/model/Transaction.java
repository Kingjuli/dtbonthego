package com.dtbonthego.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a financial transaction.
 */
@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Unique reference number for the transaction
     */
    @Column(nullable = false, unique = true)
    private String referenceNumber;
    
    /**
     * Type of transaction
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    /**
     * Amount involved in the transaction
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    /**
     * Account number from which funds are withdrawn or debited
     */
    @Column(nullable = true)
    private String sourceAccountNumber;
    
    /**
     * Account number to which funds are deposited or credited
     */
    @Column(nullable = false)
    private String destinationAccountNumber;
    
    /**
     * Current status of the transaction
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    
    /**
     * Timestamp when the transaction was initiated
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the transaction was last updated
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Optional description or purpose of the transaction
     */
    @Column(length = 255)
    private String description;
    
    /**
     * Error message in case of failure
     */
    @Column(length = 500)
    private String errorMessage;
    
    /**
     * Profile ID of the user who initiated the transaction
     */
    @Column(nullable = false)
    private Long initiatedByProfileId;
    
    /**
     * Version for optimistic locking
     */
    @Version
    private Long version;
    
    /**
     * Sets creation and update timestamps upon entity creation
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates the update timestamp upon entity modification
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 