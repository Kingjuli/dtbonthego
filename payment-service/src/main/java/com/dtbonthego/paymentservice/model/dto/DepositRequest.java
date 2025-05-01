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
 * Data Transfer Object for deposit (top-up) requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {
    
    /**
     * Account number to deposit funds into
     */
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    /**
     * Amount to deposit
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    /**
     * Optional description for the deposit
     */
    private String description;
} 