package com.dtbonthego.paymentservice.service.impl;

import com.dtbonthego.paymentservice.exception.*;
import com.dtbonthego.paymentservice.model.Transaction;
import com.dtbonthego.paymentservice.model.TransactionStatus;
import com.dtbonthego.paymentservice.model.TransactionType;
import com.dtbonthego.paymentservice.model.dto.*;
import com.dtbonthego.paymentservice.repository.TransactionRepository;
import com.dtbonthego.paymentservice.service.AccountServiceClient;
import com.dtbonthego.paymentservice.service.TransactionService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the TransactionService interface.
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountServiceClient accountServiceClient;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public TransactionResponse processDeposit(DepositRequest request, Long profileId) {
        logger.info("Processing deposit for account: {}", request.getAccountNumber());
        
        // Validate request
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw InvalidTransactionException.invalidAmount();
        }
        
        // Get the current authentication token
        String token = getCurrentAuthToken();
        
        // Verify the account exists and is active
        accountServiceClient.getAccount(request.getAccountNumber(), token);
        
        // Create a new transaction record
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = Transaction.builder()
                .referenceNumber(referenceNumber)
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .destinationAccountNumber(request.getAccountNumber())
                .status(TransactionStatus.PROCESSING)
                .description(request.getDescription())
                .initiatedByProfileId(profileId)
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        try {
            // Update the account balance
            accountServiceClient.updateBalance(request.getAccountNumber(), request.getAmount(), token);
            
            // Update transaction status to completed
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);
            
            return TransactionResponse.builder()
                    .referenceNumber(transaction.getReferenceNumber())
                    .status(TransactionStatus.COMPLETED)
                    .message("Deposit processed successfully")
                    .build();
            
        } catch (Exception e) {
            // Set transaction status to failed
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorMessage(e.getMessage());
            transactionRepository.save(transaction);
            
            // Rethrow the exception
            if (e instanceof AccountNotFoundException || 
                e instanceof InsufficientFundsException || 
                e instanceof InvalidTransactionException) {
                throw e;
            }
            
            throw new TransactionProcessingException("Failed to process deposit: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public TransactionResponse processWithdrawal(WithdrawalRequest request, Long profileId) {
        logger.info("Processing withdrawal for account: {}", request.getAccountNumber());
        
        // Validate request
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw InvalidTransactionException.invalidAmount();
        }
        
        // Get the current authentication token
        String token = getCurrentAuthToken();
        
        // Verify the account exists and is active
        accountServiceClient.getAccount(request.getAccountNumber(), token);
        
        // Create a new transaction record
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = Transaction.builder()
                .referenceNumber(referenceNumber)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .sourceAccountNumber(request.getAccountNumber())
                .destinationAccountNumber(request.getAccountNumber()) // Same for withdrawal
                .status(TransactionStatus.PROCESSING)
                .description(request.getDescription())
                .initiatedByProfileId(profileId)
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        try {
            // Update the account balance with negative amount for withdrawal
            accountServiceClient.updateBalance(request.getAccountNumber(), request.getAmount().negate(), token);
            
            // Update transaction status to completed
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);
            
            return TransactionResponse.builder()
                    .referenceNumber(transaction.getReferenceNumber())
                    .status(TransactionStatus.COMPLETED)
                    .message("Withdrawal processed successfully")
                    .build();
            
        } catch (Exception e) {
            // Set transaction status to failed
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorMessage(e.getMessage());
            transactionRepository.save(transaction);
            
            // Rethrow the exception
            if (e instanceof AccountNotFoundException || 
                e instanceof InsufficientFundsException || 
                e instanceof InvalidTransactionException) {
                throw e;
            }
            
            throw new TransactionProcessingException("Failed to process withdrawal: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public TransactionResponse processTransfer(TransferRequest request, Long profileId) {
        logger.info("Processing transfer from account: {} to account: {}", 
                request.getSourceAccountNumber(), request.getDestinationAccountNumber());
        
        // Validate request
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw InvalidTransactionException.invalidAmount();
        }
        
        if (request.getSourceAccountNumber().equals(request.getDestinationAccountNumber())) {
            throw InvalidTransactionException.sameSourceAndDestination();
        }
        
        // Get the current authentication token
        String token = getCurrentAuthToken();
        
        // Verify both accounts exist and are active
        accountServiceClient.getAccount(request.getSourceAccountNumber(), token);
        accountServiceClient.getAccount(request.getDestinationAccountNumber(), token);
        
        // Create a new transaction record
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = Transaction.builder()
                .referenceNumber(referenceNumber)
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .sourceAccountNumber(request.getSourceAccountNumber())
                .destinationAccountNumber(request.getDestinationAccountNumber())
                .status(TransactionStatus.PROCESSING)
                .description(request.getDescription())
                .initiatedByProfileId(profileId)
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        try {
            // Debit the source account
            accountServiceClient.updateBalance(request.getSourceAccountNumber(), request.getAmount().negate(), token);
            
            // Credit the destination account
            accountServiceClient.updateBalance(request.getDestinationAccountNumber(), request.getAmount(), token);
            
            // Update transaction status to completed
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);
            
            return TransactionResponse.builder()
                    .referenceNumber(transaction.getReferenceNumber())
                    .status(TransactionStatus.COMPLETED)
                    .message("Transfer processed successfully")
                    .build();
            
        } catch (Exception e) {
            // Set transaction status to failed
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorMessage(e.getMessage());
            transactionRepository.save(transaction);
            
            // If an error occurs after debiting the source account but before crediting the destination,
            // we need to refund the source account to ensure atomicity
            if (transaction.getStatus() == TransactionStatus.PROCESSING && 
                    e.getMessage() != null && 
                    !e.getMessage().contains("insufficient")) {
                try {
                    // Refund the source account
                    accountServiceClient.updateBalance(request.getSourceAccountNumber(), request.getAmount(), token);
                    
                    logger.info("Refunded source account {} after failed transfer", request.getSourceAccountNumber());
                } catch (Exception refundException) {
                    logger.error("Failed to refund source account {} after failed transfer: {}",
                            request.getSourceAccountNumber(), refundException.getMessage());
                    // In a real system, this would be sent to a queue for manual review or retry
                }
            }
            
            // Rethrow the exception
            if (e instanceof AccountNotFoundException || 
                e instanceof InsufficientFundsException || 
                e instanceof InvalidTransactionException) {
                throw e;
            }
            
            throw new TransactionProcessingException("Failed to process transfer: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionDTO getTransactionByReferenceNumber(String referenceNumber) {
        logger.debug("Getting transaction with reference number: {}", referenceNumber);
        
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new TransactionNotFoundException(referenceNumber));
        
        return mapToDTO(transaction);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> getTransactionsByAccountNumber(String accountNumber, Long profileId) {
        logger.debug("Getting transactions for account: {}", accountNumber);
        
        // Get the current authentication token
        String token = getCurrentAuthToken();
        
        // Verify the account exists and belongs to the user
        Map<String, Object> accountInfo = accountServiceClient.getAccount(accountNumber, token);
        
        // Verify the account belongs to the user
        Long accountProfileId = Long.valueOf(accountInfo.get("profileId").toString());
        if (!profileId.equals(accountProfileId)) {
            throw SecurityViolationException.unauthorizedAccountAccess(accountNumber, profileId);
        }
        
        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber);
        
        return transactions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> getTransactionsByProfileId(Long profileId) {
        logger.debug("Getting transactions for profile: {}", profileId);
        
        List<Transaction> transactions = transactionRepository.findByInitiatedByProfileId(profileId);
        
        return transactions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Maps a Transaction entity to a TransactionDTO
     * 
     * @param transaction the Transaction entity
     * @return the TransactionDTO
     */
    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .referenceNumber(transaction.getReferenceNumber())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .sourceAccountNumber(transaction.getSourceAccountNumber())
                .destinationAccountNumber(transaction.getDestinationAccountNumber())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .description(transaction.getDescription())
                .initiatedByProfileId(transaction.getInitiatedByProfileId())
                .build();
    }
    
    /**
     * Generates a unique reference number for transactions
     * 
     * @return a unique reference number
     */
    private String generateReferenceNumber() {
        // Format: TXN-yyyyMMdd-UUID (first 8 chars)
        String timestamp = LocalDateTime.now().toString().replaceAll("[^0-9]", "").substring(0, 8);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "TXN-" + timestamp + "-" + uuid;
    }
    
    /**
     * Gets the current authentication token from the request
     * 
     * @return the authentication token
     */
    private String getCurrentAuthToken() {
        try {
            ServletRequestAttributes requestAttributes = 
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes != null) {
                String authHeader = requestAttributes.getRequest().getHeader("Authorization");
                if (authHeader != null && !authHeader.isEmpty()) {
                    return authHeader;
                }
            }
            
            // If we couldn't get the token from request attributes, try from security context
            // This is just a fallback and may not work in all cases
            return "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials();
        } catch (Exception e) {
            logger.error("Failed to get authentication token", e);
            throw new SecurityViolationException("Could not get authentication token");
        }
    }
} 