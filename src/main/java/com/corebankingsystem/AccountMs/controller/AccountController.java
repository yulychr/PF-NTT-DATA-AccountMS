package com.corebankingsystem.AccountMs.controller;

import com.corebankingsystem.AccountMs.DTO.DepositRequestDTO;
import com.corebankingsystem.AccountMs.DTO.WithdrawalRequestDTO;
import com.corebankingsystem.AccountMs.model.entity.Account;
import com.corebankingsystem.AccountMs.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts/")
public class AccountController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<Object> createAccount(@Valid @RequestBody Account account) {

        try {
            String url = "http://localhost:8086/customers/" + account.getCustomerId() ;
            restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            String message = "The customer ID does not exist ";
            return ResponseEntity.status(404).body(message);
        } catch (Exception e) {
            // Manejo de otros errores posibles (por ejemplo, problemas de conexión o servicio caído)
            String errorMessage = "An error occurred while validating the customer ID";
            return ResponseEntity.status(500).body(errorMessage); // 500 Internal Server Error
        }
        Account localAccount = accountService.createAccount(
                account.getBalance(),
                account.getTypeAccount(),
                account.getCustomerId()
        );
        return  ResponseEntity.status(201).body(localAccount);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAccounts();
        return ResponseEntity.status(200).body(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountId(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountId(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<Object> deposit(@PathVariable Long id, @RequestParam Double amount) {
        return accountService.deposit(id, amount);
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<Object> withdraw(@PathVariable Long id, @RequestParam Double amount) {
        return accountService.withdraw(id, amount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAccountId(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }

    // EndPoint para saber si el cliente tiene cuentas activas
    @GetMapping ("/customer/{id}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable Long id) {
        Optional<List<Account>> account = accountService.getCustomerId(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //GET endpoint to retrieve account details by account number received from TransactionMS
    @GetMapping("/byAccountNumber/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        Optional<Account> account = accountService.getAccountByNumber(accountNumber);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account.get());
    }
    //POST endpoint to receive deposit information from TransactionMS and process deposit in the specified account
    @PostMapping("/tDeposit")
    public ResponseEntity<?> tDeposit(@RequestBody DepositRequestDTO depositRequestDTO) {
        Optional<Account> accountD = accountService.getAccountByNumber(depositRequestDTO.getAccountNumber());
        if (accountD.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        long id = accountD.get().getId();
        ResponseEntity<Object> updatedAccount = accountService.deposit(id, depositRequestDTO.getAmount());
        if (updatedAccount == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updatedAccount);
    }

    //POST endpoint to receive deposit information from TransactionMS and process Withdrawal in the specified account
    @PostMapping("/tWithdrawal")
    public ResponseEntity<?> tWithdrawal(@RequestBody WithdrawalRequestDTO withdrawalRequestDTO) {
        Optional<Account> accountD = accountService.getAccountByNumber(withdrawalRequestDTO.getAccountNumber());
        if (accountD.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        long id = accountD.get().getId();
        ResponseEntity<Object> updatedAccount = accountService.withdraw(id, withdrawalRequestDTO.getAmount());
        if (updatedAccount == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updatedAccount);
    }

}
