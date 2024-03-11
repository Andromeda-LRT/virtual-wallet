package com.virtualwallet.models.response_model_dto;

public class WalletIbanResponseDto {
    private String iban;

    public WalletIbanResponseDto() {

    }

    public WalletIbanResponseDto(String iban) {
        this.iban = iban;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

}
