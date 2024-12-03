package com.corebankingsystem.AccountMs.repository;

import com.corebankingsystem.AccountMs.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository <Account, Long> {
    boolean existsByAccountNumber(String accountNumber);
    Optional<List<Account>> findByCustomerId(Long customerId);
    Optional<Account> findByAccountNumber(String accountNumber);


}
