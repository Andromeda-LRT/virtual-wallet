package com.virtualwallet.models.input_model_dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.beans.factory.annotation.Value;

public class CardTransactionDto {
    @Min(value = 5, message = "Amount must be at least 5.0 in order to make a transaction.")
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
