package com.dtbonthego.storeofvalueservice.model.dto;

import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import com.dtbonthego.storeofvalueservice.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Account information.
 * Used for returning account data in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private String accountName;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private Long profileId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 