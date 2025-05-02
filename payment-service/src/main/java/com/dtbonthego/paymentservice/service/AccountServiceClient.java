package com.dtbonthego.paymentservice.service;

import com.dtbonthego.paymentservice.exception.AccountNotFoundException;
import com.dtbonthego.paymentservice.exception.InsufficientFundsException;
import com.dtbonthego.paymentservice.exception.InvalidTransactionException;
import com.dtbonthego.paymentservice.exception.TransactionProcessingException;
import com.dtbonthego.paymentservice.model.dto.AccountDTO;
import com.dtbonthego.paymentservice.model.dto.ProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Client service for interacting with the Account service.
 */
@Service
public class AccountServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceClient.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${store-of-value-service.url}")
    private String storeOfValueServiceUrl;
    
    @Value("${profile-service.url}")
    private String profileServiceUrl;
    
    /**
     * Get account information as an AccountDTO object
     * 
     * @param accountNumber the account number
     * @param token the authentication token
     * @return AccountDTO object
     * @throws AccountNotFoundException if the account is not found
     * @throws InvalidTransactionException if the account is not active
     */
    public AccountDTO getAccount(String accountNumber, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<AccountDTO> response = restTemplate.exchange(
                    storeOfValueServiceUrl + "/account/number/" + accountNumber,
                    HttpMethod.GET,
                    entity,
                    AccountDTO.class);
            
            AccountDTO account = response.getBody();
                        logger.info("Acc {}", account);

            // Check if the account is active
            if (account != null && !"ACTIVE".equals(account.getStatus())) {
                throw InvalidTransactionException.accountNotActive(accountNumber);
            }
            
            return account;
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Account not found: {}", accountNumber, e);
            throw new AccountNotFoundException(accountNumber);
        } catch (Exception e) {
            if (e instanceof InvalidTransactionException) {
                throw e;
            }
            logger.error("Error getting account: {}", accountNumber, e);
            throw new TransactionProcessingException("Error getting account information", e);
        }
    }
    
    /**
     * Update the account balance
     * 
     * @param accountNumber the account number
     * @param amount the amount to add (positive) or subtract (negative)
     * @param token the authentication token
     * @throws AccountNotFoundException if the account is not found
     * @throws InsufficientFundsException if the account has insufficient funds
     * @throws TransactionProcessingException if there's an error processing the update
     */
    public void updateBalance(String accountNumber, BigDecimal amount, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.set("Content-Type", "application/json");
            
            Map<String, Object> request = new HashMap<>();
            request.put("amount", amount);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            restTemplate.exchange(
                    storeOfValueServiceUrl + "/account/balance/" + accountNumber,
                    HttpMethod.PATCH,
                    entity,
                    Map.class);
            
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Account not found: {}", accountNumber, e);
            throw new AccountNotFoundException(accountNumber);
        } catch (HttpClientErrorException.BadRequest e) {
            // Assuming the bad request is due to insufficient funds
            if (e.getResponseBodyAsString().contains("insufficient")) {
                throw new InsufficientFundsException(accountNumber);
            }
            logger.error("Bad request updating balance: {}", accountNumber, e);
            throw new TransactionProcessingException("Error updating account balance", e);
        } catch (Exception e) {
            logger.error("Error updating balance: {}", accountNumber, e);
            throw new TransactionProcessingException("Error updating account balance", e);
        }
    }
    
    /**
     * Get profile information by ID
     * 
     * @param profileId the profile ID
     * @param token the authentication token
     * @return ProfileDTO object
     * @throws TransactionProcessingException if there's an error fetching the profile
     */
    public ProfileDTO getMyProfile(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<ProfileDTO> response = restTemplate.exchange(
                    profileServiceUrl + "/profile/me",
                    HttpMethod.GET,
                    entity,
                    ProfileDTO.class);
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error getting profile: {}", e);
            throw new TransactionProcessingException("Error getting profile information", e);
        }
    }
} 