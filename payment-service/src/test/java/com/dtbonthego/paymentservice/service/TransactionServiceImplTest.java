package com.dtbonthego.paymentservice.service;

import com.dtbonthego.paymentservice.exception.InsufficientFundsException;
import com.dtbonthego.paymentservice.exception.InvalidTransactionException;
import com.dtbonthego.paymentservice.model.Transaction;
import com.dtbonthego.paymentservice.model.TransactionStatus;
import com.dtbonthego.paymentservice.model.TransactionType;
import com.dtbonthego.paymentservice.model.dto.*;
import com.dtbonthego.paymentservice.repository.TransactionRepository;
import com.dtbonthego.paymentservice.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private ProfileServiceClient profileServiceClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private final Long PROFILE_ID = 101L;
    private AccountDTO sourceAccount;
    private AccountDTO destinationAccount;
    private DepositRequest depositRequest;
    private WithdrawalRequest withdrawalRequest;
    private TransferRequest transferRequest;
    private Transaction transaction;
    private ProfileDTO mockProfile;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(PROFILE_ID);
        
        sourceAccount = AccountDTO.builder()
                    .accountNumber("1012345678901")
                    .balance(BigDecimal.valueOf(1000))
                    .profileId(PROFILE_ID)
                    .status("ACTIVE")
                    .build();
                    
        destinationAccount = AccountDTO.builder()
                    .accountNumber("1087654321098")
                    .balance(BigDecimal.valueOf(500))
                    .profileId(102L)
                    .status("ACTIVE")
                    .build();
                    
        depositRequest = DepositRequest.builder()
                    .accountNumber("1012345678901")
                    .amount(BigDecimal.valueOf(500))
                    .description("Test deposit")
                    .build();
                    
        withdrawalRequest = WithdrawalRequest.builder()
                    .accountNumber("1012345678901")
                    .amount(BigDecimal.valueOf(200))
                    .description("Test withdrawal")
                    .build();
                    
        transferRequest = TransferRequest.builder()
                    .sourceAccountNumber("1012345678901")
                    .destinationAccountNumber("1087654321098")
                    .amount(BigDecimal.valueOf(300))
                    .description("Test transfer")
                    .build();
            
        transaction = Transaction.builder()
                    .id(1L)
                    .referenceNumber("TR12345678")
                    .type(TransactionType.DEPOSIT)
                    .amount(BigDecimal.valueOf(500))
                    .destinationAccountNumber("1012345678901")
                    .status(TransactionStatus.COMPLETED)
                    .description("Test deposit")
                    .initiatedByProfileId(PROFILE_ID)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
                    
        // Setup mock profile for event publishing
        mockProfile = ProfileDTO.builder()
                    .id(PROFILE_ID)
                    .email("user@example.com")
                    .firstName("Test")
                    .lastName("User")
                    .build();
                    
        // Mock the account service client to return our mock profile
        lenient().when(accountServiceClient.getMyProfile(anyString())).thenReturn(mockProfile);
        
        // Instead of mocking getProfileById which might not exist, directly mock the event publishing
        lenient().doNothing().when(kafkaProducerService).publishTransactionEvent(any(TransactionEvent.class));
    }

    @Test
    void processDeposit_WithValidRequest_ShouldReturnSuccessResponse() {
        // Arrange
        when(accountServiceClient.getAccount(eq("1012345678901"), anyString())).thenReturn(sourceAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            if (savedTransaction.getId() == null) {
                savedTransaction.setId(1L);
            }
            return savedTransaction;
        });

        // Act
        TransactionResponse response = transactionService.processDeposit(depositRequest, PROFILE_ID);
        
        // Assert
        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals("Deposit processed successfully", response.getMessage());
        
        // Verify interactions
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountServiceClient, times(1)).getAccount(eq("1012345678901"), anyString());
        verify(accountServiceClient, times(1)).updateBalance(eq("1012345678901"), any(BigDecimal.class), anyString());
    }
    
    @Test
    void processDeposit_WithInvalidAmount_ShouldThrowException() {
        // Arrange
        depositRequest.setAmount(BigDecimal.ZERO);
        
        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> 
            transactionService.processDeposit(depositRequest, PROFILE_ID)
        );
        
        // Verify no interactions
        verify(accountServiceClient, never()).getAccount(anyString(), anyString());
        verify(accountServiceClient, never()).updateBalance(anyString(), any(BigDecimal.class), anyString());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void processWithdrawal_WithValidRequest_ShouldReturnSuccessResponse() {
        // Arrange
        when(accountServiceClient.getAccount(eq("1012345678901"), anyString())).thenReturn(sourceAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            if (savedTransaction.getId() == null) {
                savedTransaction.setId(1L);
            }
            return savedTransaction;
        });
        
        // Act
        TransactionResponse response = transactionService.processWithdrawal(withdrawalRequest, PROFILE_ID);
        
        // Assert
        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals("Withdrawal processed successfully", response.getMessage());
        
        // Verify interactions
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountServiceClient, times(1)).getAccount(eq("1012345678901"), anyString());
        verify(accountServiceClient, times(1)).updateBalance(eq("1012345678901"), any(BigDecimal.class), anyString());
    }
    
    @Test
    void processWithdrawal_WithInsufficientFunds_ShouldHandleExceptionAndUpdateTransaction() {
        // Arrange
        AccountDTO lowBalanceAccount = AccountDTO.builder()
                .accountNumber("1012345678901")
                .balance(BigDecimal.valueOf(100))
                .profileId(PROFILE_ID)
                .status("ACTIVE")
                .build();
                
        WithdrawalRequest largeWithdrawalRequest = WithdrawalRequest.builder()
                .accountNumber("1012345678901")
                .amount(BigDecimal.valueOf(500))
                .description("Large withdrawal")
                .build();
        
        when(accountServiceClient.getAccount(eq("1012345678901"), anyString())).thenReturn(lowBalanceAccount);
        doThrow(new InsufficientFundsException("Insufficient funds in account"))
                .when(accountServiceClient).updateBalance(eq("1012345678901"), any(BigDecimal.class), anyString());
        
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            if (savedTransaction.getId() == null) {
                savedTransaction.setId(1L);
            }
            return savedTransaction;
        });
        
        // Act
        String expectedMessage = "Insufficient funds in account";
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            transactionService.processWithdrawal(largeWithdrawalRequest, PROFILE_ID);
        });
        
        // Assert
        assertTrue(exception.getMessage().contains(expectedMessage));
        
        // Verify interactions
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountServiceClient, times(1)).getAccount(eq("1012345678901"), anyString());
        verify(accountServiceClient, times(1)).updateBalance(eq("1012345678901"), any(BigDecimal.class), anyString());
    }

    @Test
    void processTransfer_WithValidRequest_ShouldReturnSuccessResponse() {
        // Arrange
        when(accountServiceClient.getAccount(eq("1012345678901"), anyString())).thenReturn(sourceAccount);
        when(accountServiceClient.getAccount(eq("1087654321098"), anyString())).thenReturn(destinationAccount);
        
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            if (savedTransaction.getId() == null) {
                savedTransaction.setId(1L);
            }
            return savedTransaction;
        });
        
        // Act
        TransactionResponse response = transactionService.processTransfer(transferRequest, PROFILE_ID);
        
        // Assert
        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals("Transfer processed successfully", response.getMessage());
        
        // Verify interactions
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountServiceClient, times(1)).getAccount(eq("1012345678901"), anyString());
        verify(accountServiceClient, times(1)).getAccount(eq("1087654321098"), anyString());
        verify(accountServiceClient, times(1)).updateBalance(eq("1012345678901"), any(BigDecimal.class), anyString());
        verify(accountServiceClient, times(1)).updateBalance(eq("1087654321098"), any(BigDecimal.class), anyString());
    }
    
    @Test
    void processTransfer_WithSameSourceAndDestination_ShouldThrowException() {
        // Arrange
        transferRequest.setSourceAccountNumber("1012345678901");
        transferRequest.setDestinationAccountNumber("1012345678901");
        
        // Act & Assert
        assertThrows(InvalidTransactionException.class, () -> 
            transactionService.processTransfer(transferRequest, PROFILE_ID)
        );
        
        // Verify no interactions
        verify(accountServiceClient, never()).getAccount(anyString(), anyString());
        verify(accountServiceClient, never()).updateBalance(anyString(), any(BigDecimal.class), anyString());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    
    @Test
    void getTransactionByReferenceNumber_WithValidReference_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findByReferenceNumber("TR12345678")).thenReturn(Optional.of(transaction));
        
        // Act
        TransactionDTO result = transactionService.getTransactionByReferenceNumber("TR12345678");
        
        // Assert
        assertNotNull(result);
        assertEquals("TR12345678", result.getReferenceNumber());
        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(500), result.getAmount());
        assertEquals("1012345678901", result.getDestinationAccountNumber());
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
    }
    
    @Test
    void getTransactionsByAccountNumber_WithAuthorizedUser_ShouldReturnTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(
            Transaction.builder()
                .id(1L)
                .referenceNumber("TR12345678")
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(500))
                .destinationAccountNumber("1012345678901")
                .status(TransactionStatus.COMPLETED)
                .initiatedByProfileId(PROFILE_ID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),
            Transaction.builder()
                .id(2L)
                .referenceNumber("TR87654321")
                .type(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(300))
                .sourceAccountNumber("1012345678901")
                .destinationAccountNumber("1012345678901")
                .status(TransactionStatus.COMPLETED)
                .initiatedByProfileId(PROFILE_ID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );
        
        when(accountServiceClient.getAccount(eq("1012345678901"), anyString())).thenReturn(sourceAccount);
        when(transactionRepository.findByAccountNumber("1012345678901")).thenReturn(transactions);
        
        // Act
        List<TransactionDTO> results = transactionService.getTransactionsByAccountNumber("1012345678901", PROFILE_ID);
        
        // Assert
        assertEquals(2, results.size());
        assertEquals("TR12345678", results.get(0).getReferenceNumber());
        assertEquals("TR87654321", results.get(1).getReferenceNumber());
        
        // Verify account ownership check
        verify(accountServiceClient).getAccount(eq("1012345678901"), anyString());
    }
    
    @Test
    void getTransactionsByProfileId_ShouldReturnUserTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(
            Transaction.builder()
                .id(1L)
                .referenceNumber("TR12345678")
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(500))
                .destinationAccountNumber("1012345678901")
                .status(TransactionStatus.COMPLETED)
                .initiatedByProfileId(PROFILE_ID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),
            Transaction.builder()
                .id(2L)
                .referenceNumber("TR87654321")
                .type(TransactionType.TRANSFER)
                .amount(BigDecimal.valueOf(200))
                .sourceAccountNumber("1012345678901")
                .destinationAccountNumber("1087654321098")
                .status(TransactionStatus.COMPLETED)
                .initiatedByProfileId(PROFILE_ID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );
        
        when(transactionRepository.findByInitiatedByProfileId(PROFILE_ID)).thenReturn(transactions);
        
        // Act
        List<TransactionDTO> results = transactionService.getTransactionsByProfileId(PROFILE_ID);
        
        // Assert
        assertEquals(2, results.size());
        assertEquals("TR12345678", results.get(0).getReferenceNumber());
        assertEquals("TR87654321", results.get(1).getReferenceNumber());
    }
} 