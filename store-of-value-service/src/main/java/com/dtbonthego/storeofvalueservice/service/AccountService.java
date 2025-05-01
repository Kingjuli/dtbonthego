package com.dtbonthego.storeofvalueservice.service;

import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import com.dtbonthego.storeofvalueservice.model.dto.AccountDTO;
import com.dtbonthego.storeofvalueservice.model.dto.CreateAccountRequest;
import com.dtbonthego.storeofvalueservice.model.dto.UpdateAccountRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing accounts.
 */
public interface AccountService {
    
    /**
     * Create a new account.
     * 
     * @param request the account creation request
     * @return the created account DTO
     */
    AccountDTO createAccount(CreateAccountRequest request);
    
    /**
     * Get an account by ID.
     * 
     * @param id the account ID
     * @return the account DTO
     */
    AccountDTO getAccountById(Long id);
    
    /**
     * Get an account by account number.
     * 
     * @param accountNumber the account number
     * @return the account DTO
     */
    AccountDTO getAccountByNumber(String accountNumber);
    
    /**
     * Get all accounts for a profile.
     * 
     * @param profileId the profile ID
     * @return a list of account DTOs
     */
    List<AccountDTO> getAccountsByProfileId(Long profileId);
    
    /**
     * Update an account.
     * 
     * @param id the account ID
     * @param request the account update request
     * @return the updated account DTO
     */
    AccountDTO updateAccount(Long id, UpdateAccountRequest request);
    
    /**
     * Update the status of an account.
     * 
     * @param id the account ID
     * @param status the new status
     * @return the updated account DTO
     */
    AccountDTO updateAccountStatus(Long id, AccountStatus status);
    
    /**
     * Delete an account.
     * 
     * @param id the account ID
     */
    void deleteAccount(Long id);
    
    /**
     * Update the balance of an account.
     * 
     * @param id the account ID
     * @param amount the amount to add (positive) or subtract (negative)
     */
    void updateBalance(Long id, BigDecimal amount);
} 