package com.dtbonthego.paymentservice.controller;

import com.dtbonthego.paymentservice.model.dto.*;
import com.dtbonthego.paymentservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for transaction operations.
 */
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Slf4j
@Tag(name = "Transaction Management", description = "API for managing financial transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Process a deposit (top-up) transaction
     * 
     * @param request the deposit request
     * @return the transaction response
     */
    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds", description = "Process a deposit (top-up) into an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit processed successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized account access")
    })
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        Long profileId = getAuthenticatedProfileId();
        log.info("Deposit request received for account {} from profile {}", request.getAccountNumber(), profileId);
        
        TransactionResponse response = transactionService.processDeposit(request, profileId);
        return ResponseEntity.ok(response);
    }

    /**
     * Process a withdrawal transaction
     * 
     * @param request the withdrawal request
     * @return the transaction response
     */
    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds", description = "Process a withdrawal from an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal processed successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized account access")
    })
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        Long profileId = getAuthenticatedProfileId();
        log.info("Withdrawal request received for account {} from profile {}", request.getAccountNumber(), profileId);
        
        TransactionResponse response = transactionService.processWithdrawal(request, profileId);
        return ResponseEntity.ok(response);
    }

    /**
     * Process a transfer transaction
     * 
     * @param request the transfer request
     * @return the transaction response
     */
    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds", description = "Process a transfer between accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer processed successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized account access")
    })
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        Long profileId = getAuthenticatedProfileId();
        log.info("Transfer request received from account {} to account {} from profile {}", 
                request.getSourceAccountNumber(), request.getDestinationAccountNumber(), profileId);
        
        TransactionResponse response = transactionService.processTransfer(request, profileId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a transaction by its reference number
     * 
     * @param referenceNumber the transaction reference number
     * @return the transaction DTO
     */
    @GetMapping("/{referenceNumber}")
    @Operation(summary = "Get transaction by reference number", description = "Retrieve transaction details by reference number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction",
                    content = @Content(schema = @Schema(implementation = TransactionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    public ResponseEntity<TransactionDTO> getTransactionByReferenceNumber(
            @Parameter(description = "Transaction reference number", required = true)
            @PathVariable String referenceNumber) {
        
        log.debug("Getting transaction with reference number: {}", referenceNumber);
        TransactionDTO transaction = transactionService.getTransactionByReferenceNumber(referenceNumber);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Get all transactions for a specific account
     * 
     * @param accountNumber the account number
     * @return list of transaction DTOs
     */
    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get transactions by account", description = "Retrieve all transactions for a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized account access")
    })
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountNumber(
            @Parameter(description = "Account number", required = true)
            @PathVariable String accountNumber) {
        
        Long profileId = getAuthenticatedProfileId();
        log.debug("Getting transactions for account: {} by profile: {}", accountNumber, profileId);
        
        List<TransactionDTO> transactions = transactionService.getTransactionsByAccountNumber(accountNumber, profileId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get all transactions for the authenticated user
     * 
     * @return list of transaction DTOs
     */
    @GetMapping("/my-transactions")
    @Operation(summary = "Get user transactions", description = "Retrieve all transactions initiated by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    public ResponseEntity<List<TransactionDTO>> getMyTransactions() {
        Long profileId = getAuthenticatedProfileId();
        log.debug("Getting transactions for profile: {}", profileId);
        
        List<TransactionDTO> transactions = transactionService.getTransactionsByProfileId(profileId);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Gets the profile ID of the authenticated user.
     * 
     * @return the authenticated user's profile ID
     */
    private Long getAuthenticatedProfileId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            log.error("Could not determine user profile ID from authentication", e);
            throw new RuntimeException("Authentication failure");
        }
    }
} 