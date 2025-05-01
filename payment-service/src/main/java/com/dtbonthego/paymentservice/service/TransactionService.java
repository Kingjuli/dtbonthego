package com.dtbonthego.paymentservice.service;

import com.dtbonthego.paymentservice.model.Transaction;
import com.dtbonthego.paymentservice.model.dto.*;

import java.util.List;

/**
 * Service interface for transaction operations.
 */
public interface TransactionService {
    
    /**
     * Process a deposit (top-up) transaction
     * 
     * @param request the deposit request
     * @param profileId the ID of the profile making the request
     * @return the transaction response
     */
    TransactionResponse processDeposit(DepositRequest request, Long profileId);
    
    /**
     * Process a withdrawal transaction
     * 
     * @param request the withdrawal request
     * @param profileId the ID of the profile making the request
     * @return the transaction response
     */
    TransactionResponse processWithdrawal(WithdrawalRequest request, Long profileId);
    
    /**
     * Process a transfer transaction
     * 
     * @param request the transfer request
     * @param profileId the ID of the profile making the request
     * @return the transaction response
     */
    TransactionResponse processTransfer(TransferRequest request, Long profileId);
    
    /**
     * Get a transaction by its reference number
     * 
     * @param referenceNumber the transaction reference number
     * @return the transaction DTO
     */
    TransactionDTO getTransactionByReferenceNumber(String referenceNumber);
    
    /**
     * Get all transactions for a specific account
     * 
     * @param accountNumber the account number
     * @param profileId the ID of the profile making the request
     * @return list of transaction DTOs
     */
    List<TransactionDTO> getTransactionsByAccountNumber(String accountNumber, Long profileId);
    
    /**
     * Get all transactions initiated by a specific profile
     * 
     * @param profileId the profile ID
     * @return list of transaction DTOs
     */
    List<TransactionDTO> getTransactionsByProfileId(Long profileId);
} 