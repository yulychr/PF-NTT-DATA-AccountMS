package com.corebankingsystem.AccountMs.service.impl;

import com.corebankingsystem.AccountMs.exception.CustomerNotFoundException;
import com.corebankingsystem.AccountMs.exception.InvalidBalanceException;
import com.corebankingsystem.AccountMs.model.entity.Account;
import com.corebankingsystem.AccountMs.repository.AccountRepository;
import com.corebankingsystem.AccountMs.service.AccountService;
import com.corebankingsystem.AccountMs.service.CustomerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerService customerService;


    // Metodo para crear una nueva cuenta
    @Override
    public Account createAccount(Account account) {
        if (!customerService.isValidCustomer(account.getCustomerId())) {
            throw new CustomerNotFoundException("The Customer with ID " + account.getCustomerId() + " does not exist");
        }
        if (account.getBalance() <= 0) {
            throw new InvalidBalanceException("Balance must be positive");
        }
        String accountNumber = generateAccountNumber();

        Account newAccount = new Account(
                null,
                accountNumber,
                account.getBalance(),
                account.getTypeAccount(),
                account.getCustomerId()
        );
        return accountRepository.save(newAccount);
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }
    @Override
    public Optional<Account> getAccountId(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    @Transactional
    public ResponseEntity<Object> deposit(Long accountId, Double amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("The account ID does not exist or is invalid."));
        if (amount <= 0) {
            String message = "Invalid deposit amount. Amount must be positive.";
            return ResponseEntity.status(400).body(message);
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        return ResponseEntity.status(200).body(account);
    }

    @Override
    @Transactional
    public ResponseEntity<Object> withdraw(Long accountId, Double amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("The account ID does not exist or is invalid."));
        if (amount <= 0) {
            String message = "Invalid withdraw amount. Amount must be positive.";
            return ResponseEntity.status(400).body(message);
        }
        if (account.getTypeAccount() == Account.TypeAccount.ahorros) {
            if (account.getBalance() - amount < 0) {
                String message = "Withdrawals that result in a negative balance are not allowed for savings accounts.";
                return ResponseEntity.status(422).body(message);
            }
        } else if (account.getTypeAccount() == Account.TypeAccount.corriente) {
            if (account.getBalance() - amount < -500) {
                String message = "Withdrawals exceeding minus 500 below the balance are not allowed for checking account (overdrafts are permitted).";
                return ResponseEntity.status(422).body(message);
            }
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        return ResponseEntity.status(200).body(account);
    }

    @Override
    public ResponseEntity<?> deleteAccount(Long id) {
        Optional<Account> account = getAccountId(id);
        if (account.isPresent()) {
            accountRepository.deleteById(id);
            return ResponseEntity.status(200).body("Account successfully deleted");
        } else {
            return ResponseEntity.status(404).body("Account not found");
        }
    }

    @Override
    public Optional<List<Account>> getCustomerId(Long id) {
        return accountRepository.findByCustomerId(id);
    }

    //Metodo para generar el numero de cuenta
    public String generateAccountNumber() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    @Override
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public ResponseEntity<Object> tDeposit(String accountNumber, double amount){
        Optional<Account> accountD = accountRepository.findByAccountNumber(accountNumber);
        if (accountD.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        long id = accountD.get().getId();
        ResponseEntity<Object> updatedAccount = deposit(id, amount);
        if (updatedAccount == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updatedAccount);
    }

    @Override
    public ResponseEntity<Object> tWithdrawal(String accountNumber, double amount){
        Optional<Account> accountD = accountRepository.findByAccountNumber(accountNumber);
        if (accountD.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        long id = accountD.get().getId();
        ResponseEntity<Object> updatedAccount = withdraw(id, amount);
        if (updatedAccount == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updatedAccount);
    }

}
