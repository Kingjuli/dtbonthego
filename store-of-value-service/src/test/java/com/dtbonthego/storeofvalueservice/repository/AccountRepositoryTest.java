package com.dtbonthego.storeofvalueservice.repository;

import com.dtbonthego.storeofvalueservice.model.Account;
import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import com.dtbonthego.storeofvalueservice.model.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findByAccountNumber_WithExistingAccount_ShouldReturnAccount() {
        // Arrange
        String accountNumber = "1012345678901";
        Account account = Account.builder()
                .accountName("Test Account")
                .accountNumber(accountNumber)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(101L)
                .build();
        
        entityManager.persist(account);
        entityManager.flush();
        
        // Act
        Optional<Account> result = accountRepository.findByAccountNumber(accountNumber);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(accountNumber, result.get().getAccountNumber());
        assertEquals("Test Account", result.get().getAccountName());
    }
    
    @Test
    void findByAccountNumber_WithNonExistingAccount_ShouldReturnEmpty() {
        // Act
        Optional<Account> result = accountRepository.findByAccountNumber("nonexistent");
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void findByProfileId_WithExistingAccounts_ShouldReturnAccounts() {
        // Arrange
        Long profileId = 101L;
        
        Account savings = Account.builder()
                .accountName("Savings Account")
                .accountNumber("1012345678901")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(profileId)
                .build();
        
        Account current = Account.builder()
                .accountName("Current Account")
                .accountNumber("1012345678902")
                .accountType(AccountType.CURRENT)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(2000))
                .profileId(profileId)
                .build();
        
        // Different profile
        Account otherAccount = Account.builder()
                .accountName("Other Account")
                .accountNumber("1012345678903")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(3000))
                .profileId(102L)
                .build();
        
        entityManager.persist(savings);
        entityManager.persist(current);
        entityManager.persist(otherAccount);
        entityManager.flush();
        
        // Act
        List<Account> results = accountRepository.findByProfileId(profileId);
        
        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(a -> a.getProfileId().equals(profileId)));
        assertTrue(results.stream().anyMatch(a -> a.getAccountName().equals("Savings Account")));
        assertTrue(results.stream().anyMatch(a -> a.getAccountName().equals("Current Account")));
    }
    
    @Test
    void findByProfileIdAndStatus_ShouldReturnFilteredAccounts() {
        // Arrange
        Long profileId = 101L;
        
        Account activeAccount = Account.builder()
                .accountName("Active Account")
                .accountNumber("1012345678901")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(profileId)
                .build();
        
        Account inactiveAccount = Account.builder()
                .accountName("Inactive Account")
                .accountNumber("1012345678902")
                .accountType(AccountType.CURRENT)
                .status(AccountStatus.INACTIVE)
                .balance(BigDecimal.valueOf(2000))
                .profileId(profileId)
                .build();
        
        Account anotherActiveAccount = Account.builder()
                .accountName("Another Active Account")
                .accountNumber("1012345678903")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(3000))
                .profileId(profileId)
                .build();
        
        entityManager.persist(activeAccount);
        entityManager.persist(inactiveAccount);
        entityManager.persist(anotherActiveAccount);
        entityManager.flush();
        
        // Act
        List<Account> activeAccounts = accountRepository.findByProfileIdAndStatus(profileId, AccountStatus.ACTIVE);
        List<Account> inactiveAccounts = accountRepository.findByProfileIdAndStatus(profileId, AccountStatus.INACTIVE);
        
        // Assert
        assertEquals(2, activeAccounts.size());
        assertEquals(1, inactiveAccounts.size());
        
        assertTrue(activeAccounts.stream().allMatch(a -> a.getStatus() == AccountStatus.ACTIVE));
        assertTrue(inactiveAccounts.stream().allMatch(a -> a.getStatus() == AccountStatus.INACTIVE));
    }
    
    @Test
    void existsByAccountNumber_WithExistingAccount_ShouldReturnTrue() {
        // Arrange
        String accountNumber = "1012345678901";
        Account account = Account.builder()
                .accountName("Test Account")
                .accountNumber(accountNumber)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(101L)
                .build();
        
        entityManager.persist(account);
        entityManager.flush();
        
        // Act
        boolean exists = accountRepository.existsByAccountNumber(accountNumber);
        
        // Assert
        assertTrue(exists);
    }
    
    @Test
    void existsByAccountNumber_WithNonExistingAccount_ShouldReturnFalse() {
        // Act
        boolean exists = accountRepository.existsByAccountNumber("nonexistent");
        
        // Assert
        assertFalse(exists);
    }
} 