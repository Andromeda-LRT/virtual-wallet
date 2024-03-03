package com.virtualwallet.models.model_dto;

import jakarta.validation.constraints.NotEmpty;
import org.checkerframework.checker.index.qual.Positive;

public class CardTransactionDto {
    @NotEmpty(message = "Transaction amount can't be empty.")
    @Positive()
    private double amount;

    public CardTransactionDto() {
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
