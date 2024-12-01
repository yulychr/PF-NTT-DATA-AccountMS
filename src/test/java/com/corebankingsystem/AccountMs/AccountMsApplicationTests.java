package com.corebankingsystem.AccountMs;

import com.corebankingsystem.AccountMs.model.entity.Account;
import com.corebankingsystem.AccountMs.repository.AccountRepository;
import com.corebankingsystem.AccountMs.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountMsApplicationTests {

	@Mock
	private AccountRepository accountRepository; // Mock del repositorio

	@InjectMocks
	private AccountServiceImpl accountServiceImpl; // Servicio a probar

	private Account[] accountsArray;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		accountsArray = new Account[] {
				new Account(1L, "123456", 1000.0, Account.TypeAccount.ahorros, 1L),
				new Account(2L, "654321", 1500.0, Account.TypeAccount.corriente, 2L),
				new Account(3L, "789012", 2000.0, Account.TypeAccount.ahorros, 3L)
		};
		when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(accountsArray[0]));
		when(accountRepository.findById(2L)).thenReturn(java.util.Optional.of(accountsArray[1]));
		when(accountRepository.findById(3L)).thenReturn(java.util.Optional.of(accountsArray[2]));
	}

//Test para Create Account
	@Test
	public void testCreateAccount() {
		when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
		when(accountRepository.save(any(Account.class))).thenReturn(accountsArray[0]);
		Account result = accountServiceImpl.createAccount(1000.0, Account.TypeAccount.corriente, 1L);
		assertNotNull(result);
		assertEquals(1000.0, result.getBalance());
	}

//Test for get all accounts
	@Test
	public void testGetAccounts() {
		when(accountRepository.findAll()).thenReturn(Arrays.asList(accountsArray));
		List<Account> result = accountServiceImpl.getAccounts();
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals("123456", result.get(0).getAccountNumber());
	}

//Test for get account by id
	@Test
	public void testGetAccountId() {
		when(accountRepository.findById(1L)).thenReturn(Optional.of(accountsArray[0]));
		Optional<Account> result = accountServiceImpl.getAccountId(1L);
		assertTrue(result.isPresent());
		assertEquals("123456", result.get().getAccountNumber());
	}

// Test para deposit
	@Test
	public void testDeposit_Success() {
		when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(accountsArray[0]));
		ResponseEntity<Object> response = accountServiceImpl.deposit(1L, 500.0);
		assertEquals(200, response.getStatusCodeValue());
		Account updatedAccount = (Account) response.getBody();
		assertNotNull(updatedAccount);
		assertEquals(1500.0, updatedAccount.getBalance());
		verify(accountRepository, times(1)).save(accountsArray[0]);
	}

	@Test
	public void testDeposit_NegativeAmount() {
		when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(accountsArray[0]));
		ResponseEntity<Object> response = accountServiceImpl.deposit(1L, -500.0);
		assertEquals(400, response.getStatusCodeValue());
		String message = (String) response.getBody();
		assertEquals("Invalid deposit amount. Amount must be positive.", message);
		verify(accountRepository, times(0)).save(any(Account.class));
	}

	@Test
	public void testDeposit_AccountNotFound() {
		when(accountRepository.findById(99L)).thenReturn(java.util.Optional.empty());
		Exception exception = assertThrows(RuntimeException.class, () -> {
			accountServiceImpl.deposit(99L, 500.0);
		});
		assertEquals("The account ID does not exist or is invalid.", exception.getMessage());
		verify(accountRepository, times(0)).save(any(Account.class));
	}
// Test para withdraw
	@Test
	public void testWithdraw_SuccessfulWithdrawal_AhorrosAccount() {
		ResponseEntity<Object> response = accountServiceImpl.withdraw(1L, 500.0);
		assertEquals(200, response.getStatusCodeValue());
		Account updatedAccount = (Account) response.getBody();
		assertNotNull(updatedAccount);
		assertEquals(500.0, updatedAccount.getBalance(), 0.0);
		verify(accountRepository, times(1)).save(updatedAccount);
	}

	@Test
	public void testWithdraw_InsufficientBalance_AhorrosAccount() {
		ResponseEntity<Object> response = accountServiceImpl.withdraw(1L, 2000.0);
		assertEquals(422, response.getStatusCodeValue());
		String message = (String) response.getBody();
		assertEquals("Withdrawals that result in a negative balance are not allowed for savings accounts.", message);
		verify(accountRepository, times(0)).save(any(Account.class));
	}

	@Test
	public void testWithdraw_InsufficientBalance_CorrienteAccount() {
		ResponseEntity<Object> response = accountServiceImpl.withdraw(2L, 2600.0);
		assertEquals(422, response.getStatusCodeValue());
		String message = (String) response.getBody();
		assertEquals("Withdrawals exceeding minus 500 below the balance are not allowed for checking account (overdrafts are permitted).", message);
		verify(accountRepository, times(0)).save(any(Account.class));
	}

	@Test
	public void testWithdraw_NegativeAmount() {
		ResponseEntity<Object> response = accountServiceImpl.withdraw(1L, -500.0);
		assertEquals(400, response.getStatusCodeValue());
		String message = (String) response.getBody();
		assertEquals("Invalid withdraw amount. Amount must be positive.", message);
		verify(accountRepository, times(0)).save(any(Account.class));
	}

	@Test
	public void testWithdraw_AccountNotFound() {
		when(accountRepository.findById(99L)).thenReturn(java.util.Optional.empty());
		Exception exception = assertThrows(RuntimeException.class, () -> {
			accountServiceImpl.withdraw(99L, 500.0);
		});
		assertEquals("The account ID does not exist or is invalid.", exception.getMessage());
		verify(accountRepository, times(0)).save(any(Account.class));
	}

//Test for Delete
	@Test
	public void testDeleteAccount_Success() {
		when(accountRepository.findById(1L)).thenReturn(Optional.of(accountsArray[0]));
		ResponseEntity<Object> response = accountServiceImpl.deleteAccount(1L);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Account successfully deleted", response.getBody());
		verify(accountRepository, times(1)).deleteById(1L);
	}

	@Test
	public void testDeleteAccount_AccountNotFound() {
		when(accountRepository.findById(999L)).thenReturn(Optional.empty());
		ResponseEntity<Object> response = accountServiceImpl.deleteAccount(999L);
		assertEquals(404, response.getStatusCodeValue());
		assertEquals("Account not found", response.getBody());
		verify(accountRepository, times(0)).deleteById(999L);
	}
}
