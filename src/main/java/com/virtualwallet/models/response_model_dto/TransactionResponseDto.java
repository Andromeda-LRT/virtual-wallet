package com.virtualwallet.models.response_model_dto;

import java.time.LocalDateTime;

public class TransactionResponseDto {
    private int transactionId;
    private double amount;
    private int transactionTypeId;
    private String userName;
    private String walletIban;
    private LocalDateTime time;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(int transactionId,
                                  double amount,
                                  int transactionTypeId,
                                  String userName,
                                  String walletIban,
                                  LocalDateTime time) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionTypeId = transactionTypeId;
        this.userName = userName;
        this.walletIban = walletIban;
        this.time = time;
    }

    public int getTransactionId() {
        return transactionId;
    }


    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWalletIban() {
        return walletIban;
    }

    public void setWalletIban(String walletIban) {
        this.walletIban = walletIban;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
