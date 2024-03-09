package com.virtualwallet.models.response_model_dto;

import java.util.ArrayList;
import java.util.List;

public class RecipientResponseDto {
    private String username;
    private List<WalletIbanResponseDto> walletIban;

    public RecipientResponseDto() {

    }

    public RecipientResponseDto(String username, List<WalletIbanResponseDto> walletIban) {
        this.username = username;
        this.walletIban = walletIban;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<WalletIbanResponseDto> getWalletIban() {
        return walletIban;
    }

    public void setWalletIban(List<WalletIbanResponseDto> walletIban) {
        this.walletIban = walletIban;
    }
}
