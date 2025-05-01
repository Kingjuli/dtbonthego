package com.dtbonthego.paymentservice.model.dto;

import com.dtbonthego.paymentservice.model.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for transaction response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    
    /**
     * Transaction reference number
     */
    private String referenceNumber;
    
    /**
     * Status of the transaction
     */
    private TransactionStatus status;
    
    /**
     * Message related to the transaction
     */
    private String message;
} 