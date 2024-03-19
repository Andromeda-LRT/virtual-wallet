package com.virtualwallet.models.mvc_input_model_dto;

import jakarta.validation.constraints.NotEmpty;

public class WalletUserDto {
    @NotEmpty(message = "Username can't be empty.")
    private String username;

    public WalletUserDto() {
    }

    public WalletUserDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
