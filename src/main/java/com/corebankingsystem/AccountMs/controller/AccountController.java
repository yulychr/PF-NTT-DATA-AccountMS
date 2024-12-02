package com.corebankingsystem.AccountMs.controller;

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
}
