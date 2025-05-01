package com.dtbonthego.storeofvalueservice.model.dto;

import com.dtbonthego.storeofvalueservice.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for creating a new account.
 * Contains all required fields for account creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    
    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must be at most 100 characters")
    private String accountName;
    
    @NotNull(message = "Account type is required")
    private AccountType accountType;
    
    @NotNull(message = "Profile ID is required")
    private Long profileId;
    
    private BigDecimal initialDeposit;
} 