package com.virtualwallet.models.model_dto;

import com.virtualwallet.models.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Month;
import java.time.Year;

public class CardDto {
    @NotEmpty(message = "Card number can't be empty.")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits long.")
    @Pattern(regexp = "^\\d(?: ?\\d)*$",
            message = "Input must contain only digits, single spaces between digits are allowed. Consecutive spaces are not permitted.")
    private String number;
    @NotEmpty(message = "Expiration month can't be empty.")
    private Month expirationMonth;
    @NotEmpty(message = "Expiration year can't be empty.")
    private Year expirationYear;

    @NotEmpty(message = "Card holder can't be empty.")
    @Size(min = 2, max = 30, message = "Card holder name must be between 2 and 30 symbols long.")
    @Pattern(regexp = "^[a-zA-Z]+(?: [a-zA-Z]+)*$",
            message = "Card holder name must contain only letters and single spaces between words. Consecutive spaces are not permitted.")
    private User cardHolder;
    @NotEmpty(message = "Check number can't be empty.")
    @Size(min = 3, max = 3, message = "Check number must contain only digits.")
    private int checkNumber;


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

    public User getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(User cardHolder) {
        this.cardHolder = cardHolder;
    }

    public int getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(int checkNumber) {
        this.checkNumber = checkNumber;
    }
}
