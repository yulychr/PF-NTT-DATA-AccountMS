package com.corebankingsystem.AccountMs.DTO;

public class DepositRequestDTO {
    private String accountNumber;
    private double amount;

    // Constructor, getters y setters

    public DepositRequestDTO(String accountNumber, double amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
