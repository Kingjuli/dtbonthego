package com.dtbonthego.storeofvalueservice.controller;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountDTO testAccount;
    private List<AccountDTO> accountList;
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

        testAccount = AccountDTO.builder()
                .id(1L)
                .accountName("Test Savings Account")
                .accountNumber("1012345678901")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .profileId(PROFILE_ID)
                .createdAt(LocalDateTime.now())
                .build();

        AccountDTO secondAccount = AccountDTO.builder()
                .id(2L)
                .accountName("Test Current Account")
                .accountNumber("1098765432101")
                .accountType(AccountType.CURRENT)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(2500))
                .profileId(PROFILE_ID)
                .createdAt(LocalDateTime.now())
                .build();

        accountList = Arrays.asList(testAccount, secondAccount);

        createRequest = new CreateAccountRequest();
        createRequest.setAccountName("New Test Account");
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
    void admin_getProfileAccounts_ShouldReturnAccountList() throws Exception {
        // Set up admin authentication
        setupAuthenticationContext(PROFILE_ID, "ROLE_ADMIN");
        
        when(accountService.getAccountsByProfileId(PROFILE_ID)).thenReturn(accountList);

        mockMvc.perform(get("/admin/account/profile/" + PROFILE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[0].accountType", is("SAVINGS")))
                .andExpect(jsonPath("$[1].accountType", is("CURRENT")));

        verify(accountService).getAccountsByProfileId(PROFILE_ID);
    }

    @Test
    void admin_getAccountById_ShouldReturnAccount() throws Exception {
        // Set up admin authentication
        setupAuthenticationContext(PROFILE_ID, "ROLE_ADMIN");
        
        when(accountService.getAccountById(1L)).thenReturn(testAccount);

        mockMvc.perform(get("/admin/account/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountName", is("Test Savings Account")))
                .andExpect(jsonPath("$.accountNumber", is("1012345678901")))
                .andExpect(jsonPath("$.accountType", is("SAVINGS")))
                .andExpect(jsonPath("$.balance", is(1000)));

        verify(accountService).getAccountById(1L);
    }

    @Test
    void admin_updateAccountStatus_ShouldReturnUpdatedAccount() throws Exception {
        // Set up admin authentication
        setupAuthenticationContext(PROFILE_ID, "ROLE_ADMIN");
        
        AccountDTO updatedAccount = AccountDTO.builder()
                .id(1L)
                .accountName(testAccount.getAccountName())
                .accountNumber(testAccount.getAccountNumber())
                .accountType(testAccount.getAccountType())
                .status(AccountStatus.SUSPENDED)
                .balance(testAccount.getBalance())
                .profileId(PROFILE_ID)
                .createdAt(testAccount.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        AccountStatusRequest suspendRequest = new AccountStatusRequest();
        suspendRequest.setStatus(AccountStatus.SUSPENDED);

        when(accountService.updateAccountStatus(eq(1L), eq(AccountStatus.SUSPENDED))).thenReturn(updatedAccount);

        mockMvc.perform(patch("/admin/account/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suspendRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("SUSPENDED")));

        verify(accountService).updateAccountStatus(eq(1L), eq(AccountStatus.SUSPENDED));
    }

    @Test
    void customer_createAccount_ShouldReturnCreatedAccount() throws Exception {
        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(
                AccountDTO.builder()
                        .id(3L)
                        .accountName("New Test Account")
                        .accountNumber("1023456789012")
                        .accountType(AccountType.CURRENT)
                        .status(AccountStatus.ACTIVE)
                        .balance(BigDecimal.valueOf(500))
                        .profileId(PROFILE_ID)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.accountName", is("New Test Account")))
                .andExpect(jsonPath("$.accountType", is("CURRENT")))
                .andExpect(jsonPath("$.balance", is(500)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(accountService).createAccount(any(CreateAccountRequest.class));
    }

    @Test
    void customer_getUserAccounts_ShouldReturnUserAccounts() throws Exception {
        // In a real integration test, this would need to properly inject the profile ID into the security context
        // For a mock test, we're just verifying the service is called and the response is handled correctly
        when(accountService.getAccountsByProfileId(any())).thenReturn(accountList);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountName", is("Test Savings Account")))
                .andExpect(jsonPath("$[1].accountName", is("Test Current Account")));

        verify(accountService).getAccountsByProfileId(any());
    }

    @Test
    void customer_getAccountByNumber_ShouldReturnAccount() throws Exception {
        String accountNumber = "1012345678901";
        when(accountService.getAccountByNumber(accountNumber)).thenReturn(testAccount);

        mockMvc.perform(get("/account/number/" + accountNumber))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber", is(accountNumber)))
                .andExpect(jsonPath("$.accountName", is("Test Savings Account")));

        verify(accountService).getAccountByNumber(accountNumber);
    }

    @Test
    void customer_updateAccount_ShouldReturnUpdatedAccount() throws Exception {
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
                        .createdAt(testAccount.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        mockMvc.perform(put("/account/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountName", is("Updated Account")));

        verify(accountService).getAccountById(1L);
        verify(accountService).updateAccount(eq(1L), any(UpdateAccountRequest.class));
    }
} 