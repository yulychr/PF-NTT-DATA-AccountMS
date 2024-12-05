package com.corebankingsystem.AccountMs.service;

import com.corebankingsystem.AccountMs.model.entity.Account;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    public List<Account> getAccounts();
    public Optional<Account> getAccountId(Long id);
    public ResponseEntity<Object> deposit(Long accountId, Double amount);
    public ResponseEntity<Object> withdraw(Long accountId, Double amount);
    public ResponseEntity<?> deleteAccount(Long id);
    public Account createAccount(Account account) ;
    public Optional<List<Account>> getCustomerId(Long id) ;
    public Optional<Account> getAccountByNumber(String accountNumber);
    public ResponseEntity<Object> tDeposit(String accountNumber, double amount);
    public ResponseEntity<Object> tWithdrawal(String accountNumber, double amount);

}
