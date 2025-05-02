package com.dtbonthego.storeofvalueservice.service.impl;

import com.dtbonthego.common.exception.ResourceNotFoundException;
import com.dtbonthego.storeofvalueservice.exception.InvalidAccountOperationException;
import com.dtbonthego.storeofvalueservice.model.Account;
import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import com.dtbonthego.storeofvalueservice.model.dto.AccountDTO;
import com.dtbonthego.storeofvalueservice.model.dto.CreateAccountRequest;
import com.dtbonthego.storeofvalueservice.model.dto.UpdateAccountRequest;
import com.dtbonthego.storeofvalueservice.repository.AccountRepository;
import com.dtbonthego.storeofvalueservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implementation of the AccountService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final Random random = new Random();

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AccountDTO createAccount(CreateAccountRequest request) {
        log.info("Creating new account for profile: {}", request.getProfileId());
        
        String accountNumber = generateAccountNumber();
        
        Account account = Account.builder()
                .accountName(request.getAccountName())
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .status(AccountStatus.ACTIVE)
                .balance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO)
                .profileId(request.getProfileId())
                .build();
        
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", savedAccount.getId());
        
        return mapToDTO(savedAccount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) {
        log.info("Fetching account with ID: {}", id);
        Account account = findAccountById(id);
        return mapToDTO(account);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountByNumber(String accountNumber) {
        log.info("Fetching account with number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> ResourceNotFoundException.accountNotFound(accountNumber));
        return mapToDTO(account);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByProfileId(Long profileId) {
        log.info("Fetching all accounts for profile: {}", profileId);
        List<Account> accounts = accountRepository.findByProfileId(profileId);
        return accounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AccountDTO updateAccount(Long id, UpdateAccountRequest request) {
        log.info("Updating account with ID: {}", id);
        Account account = findAccountById(id);
        
        // Check if account can be updated
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw InvalidAccountOperationException.operationOnClosedAccount(id.toString(), "update");
        }
        
        if (account.getStatus() == AccountStatus.SUSPENDED) {
            throw InvalidAccountOperationException.operationOnSuspendedAccount(id.toString(), "update");
        }
        
        if (request.getAccountName() != null) {
            account.setAccountName(request.getAccountName());
        }
        
        if (request.getAccountType() != null) {
            account.setAccountType(request.getAccountType());
        }
        
        Account updatedAccount = accountRepository.save(account);
        log.info("Account updated successfully");
        
        return mapToDTO(updatedAccount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AccountDTO updateAccountStatus(Long id, AccountStatus status) {
        log.info("Updating status of account with ID: {} to {}", id, status);
        Account account = findAccountById(id);
        
        // Validate status transition
        validateStatusTransition(account.getStatus(), status, id);
        
        account.setStatus(status);
        
        Account updatedAccount = accountRepository.save(account);
        log.info("Account status updated successfully");
        
        return mapToDTO(updatedAccount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAccount(Long id) {
        log.info("Deleting account with ID: {}", id);
        Account account = findAccountById(id);
        
        // Only allow deletion of accounts that are not active
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw InvalidAccountOperationException.invalidStatusTransition(
                    account.getStatus().name(), AccountStatus.CLOSED.name());
        }
        
        accountRepository.delete(account);
        log.info("Account deleted successfully");
    }

    /**
     * Finds an account by ID or throws an exception if not found.
     * 
     * @param id the account ID
     * @return the account
     * @throws ResourceNotFoundException if the account is not found
     */
    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.accountNotFound(id.toString()));
    }
    
    /**
     * Validates if a status transition is allowed.
     * 
     * @param currentStatus the current status of the account
     * @param newStatus the requested new status
     * @param accountId the account ID
     * @throws InvalidAccountOperationException if the status transition is not allowed
     */
    private void validateStatusTransition(AccountStatus currentStatus, AccountStatus newStatus, Long accountId) {
        if (currentStatus == AccountStatus.CLOSED) {
            throw InvalidAccountOperationException.operationOnClosedAccount(
                    accountId.toString(), "status update");
        }
        
        // Define specific illegal transitions
        if (currentStatus == AccountStatus.ACTIVE && newStatus == AccountStatus.CLOSED) {
            // An account must be inactive or suspended before it can be closed
            throw InvalidAccountOperationException.invalidStatusTransition(
                    currentStatus.toString(), newStatus.toString());
        }
    }

    /**
     * Maps an Account entity to an AccountDTO.
     * 
     * @param account the account entity
     * @return the account DTO
     */
    private AccountDTO mapToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .balance(account.getBalance())
                .profileId(account.getProfileId())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    /**
     * Generates a unique account number.
     * 
     * @return a unique account number
     */
    private String generateAccountNumber() {
        String prefix = "10";
        StringBuilder accountNumber;
        boolean accountExists;
        
        do {
            accountNumber = new StringBuilder(prefix);
            for (int i = 0; i < 10; i++) {
                accountNumber.append(random.nextInt(10));
            }
            accountExists = accountRepository.existsByAccountNumber(accountNumber.toString());
        } while (accountExists);
        
        return accountNumber.toString();
    }
    
    /**
     * Updates the balance of an account.
     * 
     * @param id the account ID
     * @param amount the amount to add (positive) or subtract (negative)
     */
    @Override
    public void updateBalance(Long id, BigDecimal amount) {
        Account account = findAccountById(id);
        
        // Check if account can be updated
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw InvalidAccountOperationException.operationOnClosedAccount(
                    id.toString(), "update balance");
        }
        
        if (account.getStatus() == AccountStatus.SUSPENDED) {
            throw InvalidAccountOperationException.operationOnSuspendedAccount(
                    id.toString(), "update balance");
        }
        
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
} 