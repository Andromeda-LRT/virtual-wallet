package com.virtualwallet.models.input_model_dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.checkerframework.checker.index.qual.Positive;

public class TransactionDto {
    @NotNull(message = "Transaction amount can't be empty.")
    @Positive
    private double amount;
//
//    @NotEmpty(message = "User id can't be empty.")
//    private int userId;

    @NotEmpty(message = "The recipient Iban of the transaction can't be empty.")
    private String iban;

//    @NotEmpty(message = "The wallet of the transaction can't be empty.")
//    private int walletId;

    public TransactionDto(){

    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

//    public int getUserId() {
//        return userId;
//    }
//
//    public int getWalletId() {
//        return walletId;
//    }
//
//    public void setWalletId(int walletId) {
//        this.walletId = walletId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
}
