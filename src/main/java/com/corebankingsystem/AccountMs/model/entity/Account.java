package com.corebankingsystem.AccountMs.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Table(name="account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="accountNumber", unique = true)
    private String accountNumber;

    @Column(name="balance")
    @NotEmpty(message = "To open an account you need an amount")
    private double balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "typeAccount", nullable = false)
    @NotEmpty(message = "Account type is required")
    private TypeAccount typeAccount;

    @Column(name="customer_id", nullable = false)
    @NotEmpty(message = "CustomerID type is required")
    private long customerId;

    public Account(Long id, String accountNumber, double balance, TypeAccount typeAccount, long customerId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.typeAccount = typeAccount;
        this.customerId = customerId;
    }
    public Account(){

    }

    public enum TypeAccount {
        ahorros,
        corriente
    }

}
