package com.virtualwallet.models.mvc_input_model_dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.virtualwallet.model_helpers.ModelConstantHelper.PASSWORD_ERROR_MESSAGE;
import static com.virtualwallet.model_helpers.ModelConstantHelper.USERNAME_ERROR_MESSAGE;

public class LoginDto {
    @NotEmpty(message = "Username can't be empty.")
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters.")
    private String username;
    @NotEmpty(message = "Password can't be empty.")
    @Size(min = 8, max = 65 ,message = "Password must be at least 8 characters long.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
