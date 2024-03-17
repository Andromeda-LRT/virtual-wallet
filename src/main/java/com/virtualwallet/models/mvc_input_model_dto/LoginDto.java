package com.virtualwallet.models.mvc_input_model_dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginDto extends WalletUserDto{

    @NotEmpty(message = "Password can't be empty.")
    private String password;

    public LoginDto() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
