package com.dtbonthego.paymentservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing an account from the Store of Value Service.
 * Contains only essential fields needed for transaction processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    
    /**
     * The account number
     */
    private String accountNumber;
    
    /**
     * The name of the account
     */
    private String accountName;
    
    /**
     * The status of the account (ACTIVE, INACTIVE, etc.)
     */
    private String status;
    
    /**
     * The current balance of the account
     */
    private BigDecimal balance;
    
    /**
     * The ID of the profile that owns this account
     */
    private Long profileId;
} 