package com.corebankingsystem.AccountMs;
import com.corebankingsystem.AccountMs.DTO.DepositRequestDTO;
import com.corebankingsystem.AccountMs.DTO.WithdrawalRequestDTO;
import com.corebankingsystem.AccountMs.controller.AccountController;
import com.corebankingsystem.AccountMs.exception.CustomerNotFoundException;
import com.corebankingsystem.AccountMs.exception.InvalidBalanceException;
import com.corebankingsystem.AccountMs.model.entity.Account;
import com.corebankingsystem.AccountMs.service.AccountService;
import com.corebankingsystem.AccountMs.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;

    private Account account;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        account = new Account();
        account.setId(1L);
        account.setBalance(1000.0);
        account.setAccountNumber("12345");
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        Account newAccount = new Account();
        newAccount.setId(2L);
        newAccount.setBalance(500.0);

        when(accountService.createAccount(any(Account.class))).thenReturn(newAccount);

        mockMvc.perform(post("/accounts/")
                        .contentType("application/json")
                        .content("{\"balance\":500.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.balance").value(500.0));

        verify(accountService, times(1)).createAccount(any(Account.class));
    }

    @Test
    void testCreateAccount_CustomerNotFound() throws Exception {
        when(accountService.createAccount(any(Account.class))).thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(post("/accounts/")
                        .contentType("application/json")
                        .content("{\"balance\":500.0}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found"));

        verify(accountService, times(1)).createAccount(any(Account.class));
    }

    @Test
    void testCreateAccount_InvalidBalance() throws Exception {
        when(accountService.createAccount(any(Account.class))).thenThrow(new InvalidBalanceException("Invalid balance"));

        mockMvc.perform(post("/accounts/")
                        .contentType("application/json")
                        .content("{\"balance\":-500.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid balance"));

        verify(accountService, times(1)).createAccount(any(Account.class));
    }

    @Test
    void testGetAccountId_Found() throws Exception {
        when(accountService.getAccountId(1L)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/accounts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.balance").value(1000.0));

        verify(accountService, times(1)).getAccountId(1L);
    }

    @Test
    void testGetAccountId_NotFound() throws Exception {
        when(accountService.getAccountId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/accounts/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getAccountId(99L);
    }

    @Test
    void testDeposit_Success() throws Exception {
        when(accountService.deposit(1L, 500.0)).thenReturn(ResponseEntity.ok("Deposit successful"));

        mockMvc.perform(put("/accounts/{id}/deposit", 1L)
                        .param("amount", "500.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful"));

        verify(accountService, times(1)).deposit(1L, 500.0);
    }

    @Test
    void testWithdraw_Success() throws Exception {
        when(accountService.withdraw(1L, 500.0)).thenReturn(ResponseEntity.ok("Withdrawal successful"));

        mockMvc.perform(put("/accounts/{id}/withdraw", 1L)
                        .param("amount", "500.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrawal successful"));

        verify(accountService, times(1)).withdraw(1L, 500.0);
    }

    @Test
    void testDeleteAccount_Success() throws Exception {
        when(accountService.deleteAccount(1L)).thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        mockMvc.perform(delete("/accounts/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).deleteAccount(1L);
    }

    @Test
    void testGetAccountsByCustomerId_Success() throws Exception {
        when(accountService.getCustomerId(1L)).thenReturn(Optional.of(List.of(account)));

        mockMvc.perform(get("/accounts/customer/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(accountService, times(1)).getCustomerId(1L);
    }

    @Test
    void testGetAccountsByCustomerId_NotFound() throws Exception {
        when(accountService.getCustomerId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/accounts/customer/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getCustomerId(99L);
    }

    @Test
    void testTDeposit_Success() throws Exception {
        DepositRequestDTO depositRequestDTO = new DepositRequestDTO("12345", 500.0);
        when(accountService.tDeposit("12345", 500.0)).thenReturn(ResponseEntity.ok("Deposit successful"));

        mockMvc.perform(post("/accounts/tDeposit")
                        .contentType("application/json")
                        .content("{\"accountNumber\":\"12345\", \"amount\":500.0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful"));

        verify(accountService, times(1)).tDeposit("12345", 500.0);
    }

    @Test
    void testTWithdrawal_Success() throws Exception {
        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO("12345", 500.0);
        when(accountService.tWithdrawal("12345", 500.0)).thenReturn(ResponseEntity.ok("Withdrawal successful"));

        mockMvc.perform(post("/accounts/tWithdrawal")
                        .contentType("application/json")
                        .content("{\"accountNumber\":\"12345\", \"amount\":500.0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrawal successful"));

        verify(accountService, times(1)).tWithdrawal("12345", 500.0);
    }
}