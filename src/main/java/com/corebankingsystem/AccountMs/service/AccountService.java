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
    public ResponseEntity<Object> deleteAccount(Long id);
    public Account createAccount(double balance, Account.TypeAccount typeAccount, long customerId) ;
    public Optional<List<Account>> getCustomerId(Long id) ;





}
