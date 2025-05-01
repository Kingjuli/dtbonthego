package com.dtbonthego.paymentservice.repository;

import com.dtbonthego.paymentservice.model.Transaction;
import com.dtbonthego.paymentservice.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find a transaction by its reference number with pessimistic lock
     * 
     * @param referenceNumber the reference number
     * @return the transaction if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    
    /**
     * Find all transactions initiated by a specific profile
     * 
     * @param profileId the profile ID
     * @return list of transactions
     */
    List<Transaction> findByInitiatedByProfileId(Long profileId);
    
    /**
     * Find all transactions for a specific account (either as source or destination)
     * 
     * @param accountNumber the account number
     * @return list of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountNumber = :accountNumber OR t.destinationAccountNumber = :accountNumber ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountNumber(String accountNumber);
    
    /**
     * Find all transactions by status
     * 
     * @param status the transaction status
     * @return list of transactions
     */
    List<Transaction> findByStatus(TransactionStatus status);
    
    /**
     * Find transactions that have been in processing state for too long
     * 
     * @param timeout the timeout threshold
     * @return list of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PROCESSING' AND t.updatedAt < :timeout")
    List<Transaction> findStuckTransactions(LocalDateTime timeout);
} 