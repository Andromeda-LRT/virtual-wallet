package com.virtualwallet.models.input_model_dto;

import java.time.LocalDateTime;

public class CardForAddingMoneyToWalletDto {
    private String number;
    private LocalDateTime expirationDate;
    private String cardHolder;
    private String checkNumber;
    private String cardType;

    public CardForAddingMoneyToWalletDto () {

    }

    public CardForAddingMoneyToWalletDto(String number, LocalDateTime expirationDate,
                   String cardHolder, String checkNumber, String cardType) {
        this.number = number;
        this.expirationDate = expirationDate;
        this.cardHolder = cardHolder;
        this.checkNumber = checkNumber;
        this.cardType = cardType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
