package com.dtbonthego.paymentservice.repository;

import com.dtbonthego.paymentservice.model.Transaction;
import com.dtbonthego.paymentservice.model.TransactionStatus;
import com.dtbonthego.paymentservice.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Simple unit tests for TransactionRepository using Mockito
 */
public class TransactionRepositoryTest {

    @Mock
    private TransactionRepository transactionRepository;

    private Transaction completedTransaction;
    private Transaction pendingTransaction;
    private Transaction stuckTransaction;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Set up test data
        completedTransaction = Transaction.builder()
                .id(1L)
                .referenceNumber("TR12345678")
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(500))
                .destinationAccountNumber("1012345678901")
                .status(TransactionStatus.COMPLETED)
                .description("Completed transaction")
                .initiatedByProfileId(101L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pendingTransaction = Transaction.builder()
                .id(2L)
                .referenceNumber("TR87654321")
                .type(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(200))
                .sourceAccountNumber("1012345678901")
                .status(TransactionStatus.PENDING)
                .description("Pending transaction")
                .initiatedByProfileId(101L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        stuckTransaction = Transaction.builder()
                .id(3L)
                .referenceNumber("TR99999999")
                .type(TransactionType.TRANSFER)
                .amount(BigDecimal.valueOf(300))
                .sourceAccountNumber("1012345678901")
                .destinationAccountNumber("1087654321098")
                .status(TransactionStatus.PROCESSING)
                .description("Stuck transaction")
                .initiatedByProfileId(102L)
                .createdAt(LocalDateTime.now().minusHours(3))
                .updatedAt(LocalDateTime.now().minusHours(3))
                .build();
    }

    @Test
    void findByReferenceNumber_WithExistingTransaction_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findByReferenceNumber("TR12345678")).thenReturn(Optional.of(completedTransaction));

        // Act
        Optional<Transaction> result = transactionRepository.findByReferenceNumber("TR12345678");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TR12345678", result.get().getReferenceNumber());
        assertEquals(TransactionStatus.COMPLETED, result.get().getStatus());
    }

    @Test
    void findByReferenceNumber_WithNonExistingTransaction_ShouldReturnEmpty() {
        // Arrange
        when(transactionRepository.findByReferenceNumber("NONEXISTENT")).thenReturn(Optional.empty());

        // Act
        Optional<Transaction> result = transactionRepository.findByReferenceNumber("NONEXISTENT");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByInitiatedByProfileId_ShouldReturnTransactionsList() {
        // Arrange
        List<Transaction> profileTransactions = Arrays.asList(completedTransaction, pendingTransaction);
        when(transactionRepository.findByInitiatedByProfileId(101L)).thenReturn(profileTransactions);

        // Act
        List<Transaction> result = transactionRepository.findByInitiatedByProfileId(101L);

        // Assert
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getInitiatedByProfileId());
        assertEquals(101L, result.get(1).getInitiatedByProfileId());
    }

    @Test
    void findByAccountNumber_ShouldReturnTransactionsForBothSourceAndDestination() {
        // Arrange
        List<Transaction> accountTransactions = Arrays.asList(completedTransaction, pendingTransaction, stuckTransaction);
        when(transactionRepository.findByAccountNumber("1012345678901")).thenReturn(accountTransactions);

        // Act
        List<Transaction> result = transactionRepository.findByAccountNumber("1012345678901");

        // Assert
        assertEquals(3, result.size());
    }

    @Test
    void findByStatus_ShouldReturnTransactionsWithSpecificStatus() {
        // Arrange
        List<Transaction> pendingTransactions = Arrays.asList(pendingTransaction);
        when(transactionRepository.findByStatus(TransactionStatus.PENDING)).thenReturn(pendingTransactions);

        // Act
        List<Transaction> result = transactionRepository.findByStatus(TransactionStatus.PENDING);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TransactionStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void findStuckTransactions_ShouldReturnTransactionsStuckInProcessing() {
        // Arrange
        LocalDateTime cutoffTime = LocalDateTime.now();
        List<Transaction> stuckTransactions = Arrays.asList(stuckTransaction);
        when(transactionRepository.findStuckTransactions(cutoffTime)).thenReturn(stuckTransactions);

        // Act
        List<Transaction> result = transactionRepository.findStuckTransactions(cutoffTime);

        // Assert
        assertEquals(1, result.size());
        assertEquals("TR99999999", result.get(0).getReferenceNumber());
    }
} 