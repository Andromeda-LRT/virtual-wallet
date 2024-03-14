package com.virtualwallet.models.response_model_dto;

import com.virtualwallet.models.Status;

import java.time.LocalDateTime;

public class TransactionResponseDto {
    private int transactionId;
    private double amount;
    private String transactionType;
    private String sender;
    private String recipient;
    private LocalDateTime time;
    private String status;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(int transactionId,
                                  double amount,
                                  String transactionType,
                                  String userName,
                                  String walletIban,
                                  LocalDateTime time, String status) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.sender = userName;
        this.recipient = walletIban;
        this.time = time;

        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
