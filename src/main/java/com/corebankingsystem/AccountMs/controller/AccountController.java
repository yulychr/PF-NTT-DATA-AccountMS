package com.corebankingsystem.AccountMs.controller;

import com.corebankingsystem.AccountMs.DTO.DepositRequestDTO;
import com.corebankingsystem.AccountMs.DTO.WithdrawalRequestDTO;
import com.corebankingsystem.AccountMs.exception.CustomerNotFoundException;
import com.corebankingsystem.AccountMs.exception.InvalidBalanceException;
import com.corebankingsystem.AccountMs.model.entity.Account;
import com.corebankingsystem.AccountMs.service.AccountService;
import com.corebankingsystem.AccountMs.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts/")
public class AccountController {

    @Autowired
    public AccountService accountService;

    @Autowired
    public CustomerService customerService;

    @PostMapping
    public ResponseEntity<Object> createAccount(@Valid @RequestBody Account account) {
        try {
            Account newAccount = accountService.createAccount(account);
            return ResponseEntity.status(201).body(newAccount);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (InvalidBalanceException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Método para obtener todas las cuentas
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAccounts();
        return ResponseEntity.status(200).body(accounts);
    }

    // Método para obtener una cuenta por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountId(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountId(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Método para hacer un depósito
    @PutMapping("/{id}/deposit")
    public ResponseEntity<Object> deposit(@PathVariable Long id, @RequestParam Double amount) {
        return accountService.deposit(id, amount);
    }

    // Método para hacer un retiro
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<Object> withdraw(@PathVariable Long id, @RequestParam Double amount) {
        return accountService.withdraw(id, amount);
    }

    // Método para eliminar una cuenta
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccountId(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }

    // Método para obtener las cuentas de un cliente por su ID (para CustomerMS)
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable Long id) {
        Optional<List<Account>> account = accountService.getCustomerId(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Método para obtener una cuenta por su número de cuenta (desde TransactionMS)
    @GetMapping("/byAccountNumber/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByNumber(accountNumber)
                .map(account -> ResponseEntity.ok(account))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para recibir información de depósito desde TransactionMS
    @PostMapping("/tDeposit")
    public ResponseEntity<?> tDeposit(@RequestBody DepositRequestDTO depositRequestDTO) {
        ResponseEntity<Object> updatedAccount = accountService.tDeposit(depositRequestDTO.getAccountNumber(), depositRequestDTO.getAmount());
        return updatedAccount;
    }

    // Endpoint para recibir información de retiro desde TransactionMS
    @PostMapping("/tWithdrawal")
    public ResponseEntity<?> tWithdrawal(@RequestBody WithdrawalRequestDTO withdrawalRequestDTO) {
        ResponseEntity<Object> updatedAccount = accountService.tWithdrawal(withdrawalRequestDTO.getAccountNumber(), withdrawalRequestDTO.getAmount());
        return updatedAccount;
    }
}

