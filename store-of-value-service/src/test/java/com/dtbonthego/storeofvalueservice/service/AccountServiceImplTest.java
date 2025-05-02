package com.dtbonthego.storeofvalueservice.service;

import com.dtbonthego.common.exception.ResourceNotFoundException;
import com.dtbonthego.storeofvalueservice.exception.InvalidAccountOperationException;
import com.dtbonthego.storeofvalueservice.model.Account;
import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import com.dtbonthego.storeofvalueservice.model.AccountType;
import com.dtbonthego.storeofvalueservice.model.dto.AccountDTO;
import com.dtbonthego.storeofvalueservice.model.dto.CreateAccountRequest;
import com.dtbonthego.storeofvalueservice.model.dto.UpdateAccountRequest;
import com.dtbonthego.storeofvalueservice.repository.AccountRepository;
import com.dtbonthego.storeofvalueservice.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private AccountDTO testAccountDTO;
    private CreateAccountRequest createRequest;
    private UpdateAccountRequest updateRequest;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .accountName("Test Account")
                .accountNumber("1012345678901")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(101L)
                .createdAt(LocalDateTime.now())
                .build();

        testAccountDTO = AccountDTO.builder()
                .id(1L)
                .accountName("Test Account")
                .accountNumber("1012345678901")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(101L)
                .createdAt(testAccount.getCreatedAt())
                .build();

        createRequest = new CreateAccountRequest();
        createRequest.setAccountName("New Account");
        createRequest.setAccountType(AccountType.CURRENT);
        createRequest.setProfileId(101L);
        createRequest.setInitialDeposit(BigDecimal.valueOf(500));

        updateRequest = new UpdateAccountRequest();
        updateRequest.setAccountName("Updated Account");
        updateRequest.setAccountType(AccountType.SAVINGS);
    }

    @Test
    void createAccount_ShouldReturnNewAccount() {
        // Arrange
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account savedAccount = invocation.getArgument(0);
            savedAccount.setId(1L);
            savedAccount.setCreatedAt(LocalDateTime.now());
            return savedAccount;
        });

        // Act
        AccountDTO result = accountService.createAccount(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(createRequest.getAccountName(), result.getAccountName());
        assertEquals(createRequest.getAccountType(), result.getAccountType());
        assertEquals(createRequest.getProfileId(), result.getProfileId());
        assertEquals(createRequest.getInitialDeposit(), result.getBalance());
        assertTrue(result.getAccountNumber().startsWith("10"));
        assertEquals(12, result.getAccountNumber().length());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void getAccountById_WithValidId_ShouldReturnAccount() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        AccountDTO result = accountService.getAccountById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        assertEquals(testAccount.getAccountName(), result.getAccountName());
        assertEquals(testAccount.getAccountNumber(), result.getAccountNumber());
        assertEquals(testAccount.getAccountType(), result.getAccountType());
        assertEquals(testAccount.getStatus(), result.getStatus());
        assertEquals(testAccount.getBalance(), result.getBalance());
        assertEquals(testAccount.getProfileId(), result.getProfileId());

        verify(accountRepository).findById(1L);
    }

    @Test
    void getAccountById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(999L));
        verify(accountRepository).findById(999L);
    }

    @Test
    void getAccountByNumber_WithValidNumber_ShouldReturnAccount() {
        // Arrange
        String accountNumber = "1012345678901";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));

        // Act
        AccountDTO result = accountService.getAccountByNumber(accountNumber);

        // Assert
        assertNotNull(result);
        assertEquals(testAccount.getAccountNumber(), result.getAccountNumber());

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void getAccountByNumber_WithInvalidNumber_ShouldThrowException() {
        // Arrange
        String invalidNumber = "9999999999";
        when(accountRepository.findByAccountNumber(invalidNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountByNumber(invalidNumber));
        verify(accountRepository).findByAccountNumber(invalidNumber);
    }

    @Test
    void getAccountsByProfileId_ShouldReturnListOfAccounts() {
        // Arrange
        long profileId = 101L;
        Account account2 = Account.builder()
                .id(2L)
                .accountName("Second Account")
                .accountNumber("1087654321098")
                .accountType(AccountType.CURRENT)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(2000))
                .profileId(profileId)
                .build();
        
        List<Account> accounts = Arrays.asList(testAccount, account2);
        when(accountRepository.findByProfileId(profileId)).thenReturn(accounts);

        // Act
        List<AccountDTO> results = accountService.getAccountsByProfileId(profileId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(testAccount.getId(), results.get(0).getId());
        assertEquals(account2.getId(), results.get(1).getId());

        verify(accountRepository).findByProfileId(profileId);
    }

    @Test
    void updateAccount_WithValidData_ShouldReturnUpdatedAccount() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        AccountDTO result = accountService.updateAccount(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(updateRequest.getAccountName(), result.getAccountName());
        assertEquals(updateRequest.getAccountType(), result.getAccountType());

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(testAccount);
    }

    @Test
    void updateAccount_WithClosedAccount_ShouldThrowException() {
        // Arrange
        testAccount.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(InvalidAccountOperationException.class, () -> accountService.updateAccount(1L, updateRequest));
        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccountStatus_WithValidTransition_ShouldReturnUpdatedAccount() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        AccountDTO result = accountService.updateAccountStatus(1L, AccountStatus.INACTIVE);

        // Assert
        assertNotNull(result);
        assertEquals(AccountStatus.INACTIVE, result.getStatus());

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(testAccount);
    }

    @Test
    void updateAccountStatus_WithInvalidTransition_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(InvalidAccountOperationException.class, 
                () -> accountService.updateAccountStatus(1L, AccountStatus.CLOSED));
        
        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateBalance_ShouldUpdateAccountBalance() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        BigDecimal originalBalance = testAccount.getBalance();
        BigDecimal amountToAdd = BigDecimal.valueOf(500);

        // Act
        accountService.updateBalance(1L, amountToAdd);

        // Assert
        assertEquals(originalBalance.add(amountToAdd), testAccount.getBalance());
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(testAccount);
    }

    @Test
    void deleteAccount_WithInactiveAccount_ShouldDeleteAccount() {
        // Arrange
        testAccount.setStatus(AccountStatus.INACTIVE);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        doNothing().when(accountRepository).delete(testAccount);

        // Act
        accountService.deleteAccount(1L);

        // Assert
        verify(accountRepository).findById(1L);
        verify(accountRepository).delete(testAccount);
    }

    @Test
    void deleteAccount_WithActiveAccount_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(InvalidAccountOperationException.class, () -> accountService.deleteAccount(1L));
        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).delete(any(Account.class));
    }
} 