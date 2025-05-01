package com.dtbonthego.storeofvalueservice.controller;

import com.dtbonthego.storeofvalueservice.model.dto.AccountDTO;
import com.dtbonthego.storeofvalueservice.model.dto.AccountStatusRequest;
import com.dtbonthego.storeofvalueservice.model.dto.CreateAccountRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for administrative management of bank accounts.
 * Provides endpoints with admin privileges for account operations.
 */
@RestController
@RequestMapping("/admin/account")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Account Management", description = "Administrative API for managing bank accounts")
public class AdminAccountController {

    private final AccountService accountService;

    /**
     * Create a new account.
     * 
     * @param request the account creation request
     * @return the created account
     */
    @PostMapping
    @Operation(summary = "Create a new account", description = "Creates a new bank account (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountDTO account = accountService.createAccount(request);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    /**
     * Get an account by ID.
     * 
     * @param id the account ID
     * @return the account
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieves an account by its ID (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> getAccountById(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id) {
        AccountDTO account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    /**
     * Get an account by account number.
     * 
     * @param accountNumber the account number
     * @return the account
     */
    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Get account by account number", description = "Retrieves an account by its account number (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> getAccountByNumber(
            @Parameter(description = "Account number", required = true)
            @PathVariable String accountNumber) {
        AccountDTO account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    /**
     * Get all accounts for a profile.
     * 
     * @param profileId the profile ID
     * @return a list of accounts
     */
    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get accounts by profile ID", description = "Retrieves all accounts for a user profile (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved accounts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<AccountDTO>> getAccountsByProfileId(
            @Parameter(description = "Profile ID", required = true)
            @PathVariable Long profileId) {
        List<AccountDTO> accounts = accountService.getAccountsByProfileId(profileId);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Update an account.
     * 
     * @param id the account ID
     * @param request the account update request
     * @return the updated account
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an account", description = "Updates an existing account (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> updateAccount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountRequest request) {
        AccountDTO account = accountService.updateAccount(id, request);
        return ResponseEntity.ok(account);
    }

    /**
     * Update the status of an account.
     * 
     * @param id the account ID
     * @param request the account status request
     * @return the updated account
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update account status", description = "Activates, deactivates, suspends, or closes an account (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account status updated successfully",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AccountDTO> updateAccountStatus(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody AccountStatusRequest request) {
        AccountDTO account = accountService.updateAccountStatus(id, request.getStatus());
        return ResponseEntity.ok(account);
    }

    /**
     * Delete an account.
     * 
     * @param id the account ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an account", description = "Deletes an account (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}