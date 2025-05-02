package com.dtbonthego.paymentservice.controller;

import com.dtbonthego.paymentservice.model.TransactionStatus;
import com.dtbonthego.paymentservice.model.dto.DepositRequest;
import com.dtbonthego.paymentservice.model.dto.TransactionResponse;
import com.dtbonthego.paymentservice.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Simple standalone test for TransactionController
 */
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    private final Long PROFILE_ID = 101L;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Mock security context for PreAuthorize annotation
        Authentication authentication = new TestingAuthenticationToken(
                PROFILE_ID, 
                null, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .build();
    }

    @Test
    public void testDeposit() throws Exception {
        // Setup test data
        DepositRequest request = new DepositRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setDescription("Test deposit");

        TransactionResponse response = TransactionResponse.builder()
                .referenceNumber("TR123456")
                .status(TransactionStatus.COMPLETED)
                .message("Deposit successful")
                .build();

        // Mock service behavior
        when(transactionService.processDeposit(any(DepositRequest.class), eq(PROFILE_ID)))
                .thenReturn(response);

        // Perform and verify test
        mockMvc.perform(post("/transaction/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber").value("TR123456"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.message").value("Deposit successful"));
    }
} 