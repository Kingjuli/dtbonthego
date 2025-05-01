package com.dtbonthego.storeofvalueservice.model.dto;

import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating the status of an account.
 * Used for activation, deactivation, suspension, or closing of accounts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusRequest {
    
    @NotNull(message = "Account status is required")
    private AccountStatus status;
} 