package com.dtbonthego.storeofvalueservice.model.dto;

import com.dtbonthego.storeofvalueservice.model.AccountType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating an existing account.
 * Contains fields that can be updated for an account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest {
    
    @Size(max = 100, message = "Account name must be at most 100 characters")
    private String accountName;
    
    private AccountType accountType;
} 