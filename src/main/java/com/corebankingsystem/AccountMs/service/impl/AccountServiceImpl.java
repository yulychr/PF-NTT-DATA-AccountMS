package com.corebankingsystem.AccountMs.service.impl;

import com.corebankingsystem.AccountMs.model.entity.Account;
import com.corebankingsystem.AccountMs.repository.AccountRepository;
import com.corebankingsystem.AccountMs.service.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    // Metodo para crear una nueva cuenta
    @Override
    public Account createAccount(double balance, Account.TypeAccount typeAccount, long customerId) {
        String accountNumber = generateAccountNumber();
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = generateAccountNumber(); // Genera un nuevo número si ya existe
        }
        Account account = new Account(null, accountNumber, balance, typeAccount, customerId);
        return accountRepository.save(account);
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
        account.deposit(amount);
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
        account.withdraw(amount);
        accountRepository.save(account);
        return ResponseEntity.status(200).body(account);
    }

    @Override
    public ResponseEntity<Object> deleteAccount(Long id) {
        Optional<Account> account = getAccountId(id);
        if (account.isPresent()){
            accountRepository.deleteById(id);
            String message = "Account successfully deleted";
            return ResponseEntity.status(200).body(message);
        }
        String message = "Account not found";
        return ResponseEntity.status(404).body(message);
    }

    @Override
    public Optional<List<Account>> getCustomerId(Long id) {
        return accountRepository.findByCustomerId(id);
    }

    //Metodo para generar el numero de cuenta
    private String generateAccountNumber() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // Genera un número entre 100000 y 999999
        return String.valueOf(number);
    }

    @Override
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
}
