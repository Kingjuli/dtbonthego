package com.dtbonthego.storeofvalueservice.controller;

import com.dtbonthego.storeofvalueservice.exception.InvalidAccountOperationException;
import com.dtbonthego.storeofvalueservice.exception.SecurityViolationException;
import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import com.dtbonthego.storeofvalueservice.model.dto.AccountDTO;
import com.dtbonthego.storeofvalueservice.model.dto.AccountStatusRequest;
import com.dtbonthego.storeofvalueservice.model.dto.CreateAccountRequest;
import com.dtbonthego.storeofvalueservice.model.dto.UpdateAccountBalanceRequest;
import com.dtbonthego.storeofvalueservice.model.dto.UpdateAccountRequest;
import com.dtbonthego.storeofvalueservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user's bank account management.
 * Ensures users can only access and modify their own accounts.
 */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Slf4j
@Tag(name = "User Account Management", description = "API for users to manage their bank accounts")
public class UserAccountController {

    private final AccountService accountService;

    /**
     * Create a new account for the authenticated user.
     * 
     * @param request the account creation request
     * @return the created account
     */
    @PostMapping
    @Operation(summary = "Create a new account", description = "Creates a new bank account for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        // Set the profileId to the authenticated user's ID
        // This ensures users can only create accounts for themselves
        Long authenticatedProfileId = getAuthenticatedProfileId();
        log.info("Creating new account for authenticated user ID: {}", authenticatedProfileId);
        
        request.setProfileId(authenticatedProfileId);
        
        AccountDTO account = accountService.createAccount(request);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    /**
     * Get one of the user's accounts by ID.
     * 
     * @param id the account ID
     * @return the account if it belongs to the user
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user's account by ID", description = "Retrieves one of the user's accounts by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found or doesn't belong to user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> getAccountById(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id) {
        log.debug("Retrieving account with ID: {} for authenticated user", id);
        AccountDTO account = accountService.getAccountById(id);
        // Verify the account belongs to the authenticated user
        verifyAccountOwnership(account);
        return ResponseEntity.ok(account);
    }

    /**
     * Get one of the user's accounts by account number.
     * 
     * @param accountNumber the account number
     * @return the account if it belongs to the user
     */
    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Get user's account by account number", description = "Retrieves one of the user's accounts by its account number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found or doesn't belong to user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> getAccountByNumber(
            @Parameter(description = "Account number", required = true)
            @PathVariable String accountNumber) {
        log.debug("Retrieving account with number: {} for authenticated user", accountNumber);
        AccountDTO account = accountService.getAccountByNumber(accountNumber);
        // Verify the account belongs to the authenticated user
        verifyAccountOwnership(account);
        return ResponseEntity.ok(account);
    }

    /**
     * Get all accounts for the authenticated user.
     * 
     * @return a list of the user's accounts
     */
    @GetMapping
    @Operation(summary = "Get all user's accounts", description = "Retrieves all accounts for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved accounts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<AccountDTO>> getUserAccounts() {
        Long authenticatedProfileId = getAuthenticatedProfileId();
        log.debug("Retrieving all accounts for authenticated user ID: {}", authenticatedProfileId);
        List<AccountDTO> accounts = accountService.getAccountsByProfileId(authenticatedProfileId);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Update one of the user's accounts.
     * 
     * @param id the account ID
     * @param request the account update request
     * @return the updated account
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user's account", description = "Updates one of the user's existing accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found or doesn't belong to user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> updateAccount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountRequest request) {
        log.debug("Updating account with ID: {} for authenticated user", id);
        
        // Verify ownership before update
        AccountDTO existingAccount = accountService.getAccountById(id);
        verifyAccountOwnership(existingAccount);
        
        // Check if account is in a state that allows updates
        if (existingAccount.getStatus() == AccountStatus.CLOSED) {
            throw InvalidAccountOperationException.operationOnClosedAccount(
                    id.toString(), "update");
        }
        
        if (existingAccount.getStatus() == AccountStatus.SUSPENDED) {
            throw InvalidAccountOperationException.operationOnSuspendedAccount(
                    id.toString(), "update");
        }
        
        AccountDTO updatedAccount = accountService.updateAccount(id, request);
        return ResponseEntity.ok(updatedAccount);
    }

    
    /**
     * Update the balance of one of the user's accounts.Users can only increase or decrease the balance of their accounts.
     * 
     * @param accountNumber the account number
     * @param request the account balance update request
     * @return 
     */
    @PatchMapping("/balance/{accountNumber}")
    @Operation(summary = "Update user's account balance", description = "Users can only increase or decrease the balance of their accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account balance updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or unauthorized balance change"),
            @ApiResponse(responseCode = "404", description = "Account not found or doesn't belong to user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> updateAccountBalance(
            @Parameter(description = "Account number", required = true)
            @PathVariable String accountNumber,
            @Parameter(description = "Amount to add (positive)", required = true)
            @RequestBody UpdateAccountBalanceRequest request) {
        log.debug("Updating balance of account with number: {} by {} for authenticated user", accountNumber, request.getAmount());
        
        // Verify ownership before update
        AccountDTO existingAccount = accountService.getAccountByNumber(accountNumber);
        verifyAccountOwnership(existingAccount);
        
        // Check if account is in a state that allows balance changes
        if (existingAccount.getStatus() == AccountStatus.CLOSED) {
            throw InvalidAccountOperationException.operationOnClosedAccount(
                    accountNumber, "update balance");
        }
        
        if (existingAccount.getStatus() == AccountStatus.SUSPENDED) {
            throw InvalidAccountOperationException.operationOnSuspendedAccount(
                    accountNumber, "update balance");
        }
        
        accountService.updateBalance(existingAccount.getId(), request.getAmount());
        return ResponseEntity.ok().build();
    }


    /**
     * Update the status of one of the user's accounts.
     * Users can only deactivate accounts, not suspend or close them.
     * 
     * @param id the account ID
     * @param request the account status request
     * @return the updated account
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user's account status", description = "Users can only activate or deactivate their accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account status updated successfully",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or unauthorized status change"),
            @ApiResponse(responseCode = "404", description = "Account not found or doesn't belong to user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> updateAccountStatus(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody AccountStatusRequest request) {
        log.debug("Updating status of account with ID: {} to {} for authenticated user", 
                id, request.getStatus());
        
        // Verify ownership before update
        AccountDTO existingAccount = accountService.getAccountById(id);
        verifyAccountOwnership(existingAccount);
        
        // Check if account is in a state that allows status changes
        if (existingAccount.getStatus() == AccountStatus.CLOSED) {
            throw InvalidAccountOperationException.operationOnClosedAccount(
                    id.toString(), "status update");
        }
        
        // Users can only activate or deactivate accounts, not suspend or close them
        switch (request.getStatus()) {
            case ACTIVE:
            case INACTIVE:
                // These status changes are allowed
                break;
            default:
                throw SecurityViolationException.unauthorizedStatusChange(
                        id.toString(), request.getStatus().toString());
        }
        
        AccountDTO updatedAccount = accountService.updateAccountStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedAccount);
    }

    /**
     * Gets the profile ID of the authenticated user.
     * 
     * @return the authenticated user's profile ID
     */
    private Long getAuthenticatedProfileId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // The principal contains the profileId as a string
        try {
            return Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            log.error("Could not determine user profile ID from authentication", e);
            throw SecurityViolationException.authenticationFailure();
        }
    }

    /**
     * Verifies that an account belongs to the authenticated user.
     * 
     * @param account the account to verify
     * @throws SecurityViolationException if the account doesn't belong to the authenticated user
     */
    private void verifyAccountOwnership(AccountDTO account) {
        Long authenticatedProfileId = getAuthenticatedProfileId();
        if (!authenticatedProfileId.equals(account.getProfileId())) {
            log.warn("Access attempt to account {} by unauthorized user {}", 
                    account.getId(), authenticatedProfileId);
            throw SecurityViolationException.unauthorizedAccountAccess(
                    account.getId().toString(), authenticatedProfileId);
        }
    }
} 