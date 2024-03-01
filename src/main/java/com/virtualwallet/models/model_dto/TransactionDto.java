package com.virtualwallet.models.model_dto;

import jakarta.validation.constraints.NotEmpty;
import org.checkerframework.checker.index.qual.Positive;

public class TransactionDto {
    @NotEmpty(message = "Transaction amount can't be empty.")
    @Positive()
    private double amount;

    @NotEmpty(message = "User id can't be empty.")
    private int userId;

    @NotEmpty(message = "The recipient wallet of the transaction can't be empty.")
    private int recipientWalletId;

    @NotEmpty(message = "The wallet of the transaction can't be empty.")
    private int walletId;

    public TransactionDto(){

    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getRecipientWalletId() {
        return recipientWalletId;
    }

    public void setRecipientWalletId(int recipientWalletId) {
        this.recipientWalletId = recipientWalletId;
    }

    public int getUserId() {
        return userId;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
