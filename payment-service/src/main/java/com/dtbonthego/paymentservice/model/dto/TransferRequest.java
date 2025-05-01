package com.dtbonthego.paymentservice.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for transfer requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    
    /**
     * Account number to transfer funds from
     */
    @NotBlank(message = "Source account number is required")
    private String sourceAccountNumber;
    
    /**
     * Account number to transfer funds to
     */
    @NotBlank(message = "Destination account number is required")
    private String destinationAccountNumber;
    
    /**
     * Amount to transfer
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    /**
     * Optional description for the transfer
     */
    private String description;
} 