package com.virtualwallet.models.mvc_input_model_dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import static com.virtualwallet.model_helpers.ModelConstantHelper.PASSWORD_ERROR_MESSAGE;
import static com.virtualwallet.model_helpers.ModelConstantHelper.USERNAME_ERROR_MESSAGE;

public class LoginDto {

    @NotEmpty(message = "Username can't be empty.")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,16}$",
            message = USERNAME_ERROR_MESSAGE)
    private String username;
    @NotEmpty(message = "Password can't be empty.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{4,20}$",
            message = PASSWORD_ERROR_MESSAGE)
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
