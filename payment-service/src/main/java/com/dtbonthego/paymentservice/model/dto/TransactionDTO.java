package com.dtbonthego.paymentservice.model.dto;

import com.dtbonthego.paymentservice.model.TransactionStatus;
import com.dtbonthego.paymentservice.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Transaction data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String referenceNumber;
    private TransactionType type;
    private BigDecimal amount;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
    private Long initiatedByProfileId;
} 