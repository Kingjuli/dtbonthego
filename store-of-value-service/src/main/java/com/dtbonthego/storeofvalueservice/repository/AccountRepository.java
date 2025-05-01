package com.dtbonthego.storeofvalueservice.repository;

import com.dtbonthego.storeofvalueservice.model.Account;
import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Account entity.
 * Provides methods to interact with the accounts table in the database.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Find an account by its account number.
     * 
     * @param accountNumber the account number to search for
     * @return an Optional containing the account if found, or empty otherwise
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Find all accounts belonging to a specific profile.
     * 
     * @param profileId the ID of the profile to find accounts for
     * @return a list of accounts associated with the profile
     */
    List<Account> findByProfileId(Long profileId);
    
    /**
     * Find all accounts belonging to a specific profile with a specific status.
     * 
     * @param profileId the ID of the profile to find accounts for
     * @param status the status of accounts to find
     * @return a list of accounts with the given status associated with the profile
     */
    List<Account> findByProfileIdAndStatus(Long profileId, AccountStatus status);
    
    /**
     * Check if an account number already exists.
     * 
     * @param accountNumber the account number to check
     * @return true if the account number exists, false otherwise
     */
    boolean existsByAccountNumber(String accountNumber);
} 