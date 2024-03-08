package com.virtualwallet.models.model_dto;

import com.virtualwallet.models.CardType;
import com.virtualwallet.models.CheckNumber;

import java.time.LocalDateTime;

public class CardResponseDto {
    private String number;
    private LocalDateTime expirationDate;
    private String cardHolder;
    private String checkNumber;
    private String cardType;

    public CardResponseDto() {
    }

    public CardResponseDto(String number, LocalDateTime expirationDate, String cardHolder, String checkNumber, String cardType) {
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
