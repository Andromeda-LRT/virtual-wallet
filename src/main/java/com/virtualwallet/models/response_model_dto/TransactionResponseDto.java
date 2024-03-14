package com.virtualwallet.models.response_model_dto;

import java.time.LocalDateTime;

public class TransactionResponseDto {
    private int transactionId;
    private double amount;
    private String transactionType;
    private String sender;
    private String recipient;
    private LocalDateTime time;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(int transactionId,
                                  double amount,
                                  String transactionType,
                                  String userName,
                                  String walletIban,
                                  LocalDateTime time) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.sender = userName;
        this.recipient = walletIban;
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

}
