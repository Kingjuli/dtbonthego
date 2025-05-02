package com.dtbonthego.storeofvalueservice.controller;

import com.dtbonthego.storeofvalueservice.exception.SecurityViolationException;
import com.dtbonthego.storeofvalueservice.model.AccountStatus;
import com.dtbonthego.storeofvalueservice.model.AccountType;
import com.dtbonthego.storeofvalueservice.model.dto.AccountDTO;
import com.dtbonthego.storeofvalueservice.model.dto.AccountStatusRequest;
import com.dtbonthego.storeofvalueservice.model.dto.CreateAccountRequest;
import com.dtbonthego.storeofvalueservice.model.dto.UpdateAccountRequest;
import com.dtbonthego.storeofvalueservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAccountController.class)
@ActiveProfiles("test")
class UserAccountControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountDTO testAccount;
    private CreateAccountRequest createRequest;
    private UpdateAccountRequest updateRequest;
    private AccountStatusRequest statusRequest;
    
    private final Long PROFILE_ID = 101L;

    @BeforeEach
    void setUp() {
        // Set up MockMvc with proper security configuration
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Configure security context with the PROFILE_ID
        setupAuthenticationContext(PROFILE_ID, "ROLE_CUSTOMER");

        // Set up test data
        testAccount = AccountDTO.builder()
                .id(1L)
                .accountName("Test Account")
                .accountNumber("1012345678901")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(PROFILE_ID)
                .createdAt(LocalDateTime.now())
                .build();

        createRequest = new CreateAccountRequest();
        createRequest.setAccountName("New Account");
        createRequest.setAccountType(AccountType.CURRENT);
        createRequest.setInitialDeposit(BigDecimal.valueOf(500));
        createRequest.setProfileId(PROFILE_ID);

        updateRequest = new UpdateAccountRequest();
        updateRequest.setAccountName("Updated Account");
        updateRequest.setAccountType(AccountType.SAVINGS);
        
        statusRequest = new AccountStatusRequest();
        statusRequest.setStatus(AccountStatus.INACTIVE);
    }

    /**
     * Helper method to set up the authentication context for tests
     */
    private void setupAuthenticationContext(Long profileId, String... roles) {
        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .toList();
        
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                profileId.toString(), "password", authorities);
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createAccount_ShouldReturnCreatedAccount() throws Exception {
        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(testAccount);

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountName", is(testAccount.getAccountName())))
                .andExpect(jsonPath("$.accountNumber", is(testAccount.getAccountNumber())))
                .andExpect(jsonPath("$.accountType", is(testAccount.getAccountType().toString())))
                .andExpect(jsonPath("$.status", is(testAccount.getStatus().toString())))
                .andExpect(jsonPath("$.balance", is(1000)))
                .andExpect(jsonPath("$.profileId", is(PROFILE_ID.intValue())));

        verify(accountService).createAccount(any(CreateAccountRequest.class));
    }

    @Test
    void getAccountById_WithAuthorizedUser_ShouldReturnAccount() throws Exception {
        when(accountService.getAccountById(1L)).thenReturn(testAccount);

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountName", is(testAccount.getAccountName())))
                .andExpect(jsonPath("$.accountNumber", is(testAccount.getAccountNumber())));

        verify(accountService).getAccountById(1L);
    }

    @Test
    void getAccountById_WithUnauthorizedUser_ShouldReturnForbidden() throws Exception {
        // Create an account belonging to a different user
        AccountDTO differentUserAccount = AccountDTO.builder()
                .id(1L)
                .accountName("Test Account")
                .accountNumber("1012345678901")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(999L) // Different profile ID
                .createdAt(LocalDateTime.now())
                .build();
        
        when(accountService.getAccountById(1L)).thenReturn(differentUserAccount);
        doThrow(new SecurityViolationException("Not authorized to access this account"))
                .when(accountService).getAccountById(1L);

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isForbidden());

        verify(accountService).getAccountById(1L);
    }

    @Test
    void getAccountByNumber_WithAuthorizedUser_ShouldReturnAccount() throws Exception {
        String accountNumber = "1012345678901";
        when(accountService.getAccountByNumber(accountNumber)).thenReturn(testAccount);

        mockMvc.perform(get("/account/number/" + accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountNumber", is(accountNumber)));

        verify(accountService).getAccountByNumber(accountNumber);
    }

    @Test
    void getUserAccounts_ShouldReturnUserAccounts() throws Exception {
        List<AccountDTO> accounts = Arrays.asList(
                testAccount,
                AccountDTO.builder()
                        .id(2L)
                        .accountName("Second Account")
                        .accountNumber("1087654321098")
                        .accountType(AccountType.CURRENT)
                        .status(AccountStatus.ACTIVE)
                        .balance(BigDecimal.valueOf(2000))
                        .profileId(PROFILE_ID)
                        .build()
        );
        
        when(accountService.getAccountsByProfileId(PROFILE_ID)).thenReturn(accounts);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(accountService).getAccountsByProfileId(PROFILE_ID);
    }

    @Test
    void updateAccount_WithAuthorizedUser_ShouldReturnUpdatedAccount() throws Exception {
        when(accountService.getAccountById(1L)).thenReturn(testAccount);
        when(accountService.updateAccount(eq(1L), any(UpdateAccountRequest.class))).thenReturn(
                AccountDTO.builder()
                        .id(1L)
                        .accountName("Updated Account")
                        .accountNumber("1012345678901")
                        .accountType(AccountType.SAVINGS)
                        .status(AccountStatus.ACTIVE)
                        .balance(BigDecimal.valueOf(1000))
                        .profileId(PROFILE_ID)
                        .build()
        );

        mockMvc.perform(put("/account/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName", is("Updated Account")))
                .andExpect(jsonPath("$.accountType", is("SAVINGS")));

        verify(accountService).getAccountById(1L);
        verify(accountService).updateAccount(eq(1L), any(UpdateAccountRequest.class));
    }

    @Test
    void updateAccountStatus_WithAuthorizedUser_ShouldReturnUpdatedAccount() throws Exception {
        when(accountService.getAccountById(1L)).thenReturn(testAccount);
        
        AccountDTO updatedAccount = AccountDTO.builder()
                .id(1L)
                .accountName(testAccount.getAccountName())
                .accountNumber(testAccount.getAccountNumber())
                .accountType(testAccount.getAccountType())
                .status(AccountStatus.INACTIVE)
                .balance(testAccount.getBalance())
                .profileId(PROFILE_ID)
                .createdAt(testAccount.getCreatedAt())
                .build();
                
        when(accountService.updateAccountStatus(eq(1L), any(AccountStatus.class))).thenReturn(updatedAccount);

        mockMvc.perform(patch("/account/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));

        verify(accountService).getAccountById(1L);
        verify(accountService).updateAccountStatus(eq(1L), eq(AccountStatus.INACTIVE));
    }
} 