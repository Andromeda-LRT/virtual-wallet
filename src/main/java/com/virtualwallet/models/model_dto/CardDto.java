package com.virtualwallet.models.model_dto;

import jakarta.validation.constraints.*;

import java.time.Month;
import java.time.Year;

public class CardDto {
    @NotEmpty(message = "Card number can't be empty.")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits long.")
    @Pattern(regexp = "^[0-9]+$", message = "The field must contain only digits.")
    private String number;
    @NotNull(message = "Expiration month can't be empty.")
    private Month expirationMonth;

    @NotNull(message = "Expiration year can't be empty.")
    private Year expirationYear;

    @NotEmpty(message = "Card holder can't be empty.")
    @Size(min = 2, max = 30, message = "Card holder name must be between 2 and 30 symbols long.")
    @Pattern(regexp = "^[a-zA-Z]+(?: [a-zA-Z]+)*$",
            message = "Card holder name must contain only letters and single spaces between words. Consecutive spaces are not permitted.")
    private String cardHolder;
    @NotEmpty(message = "Check number can't be empty.")
    @Size(min = 3, max = 3, message = "Check number must contain only digits.")
    private String checkNumber;

    @Min(value = 1, message = "Card type cannot be empty.")
    @Positive(message = "Card type id must be a positive number.")
    private int cardType;

    public CardDto() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Month getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Month expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Year getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Year expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }
}
