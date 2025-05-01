package com.dtbonthego.storeofvalueservice.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating an existing account.
 * Contains fields that can be updated for an account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountBalanceRequest {

    @NotNull
    private BigDecimal amount;
} 