package com.virtualwallet.models.input_model_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

public class UserDto extends UpdateUserDto {


    @Schema(name = "username", example = "testUsername", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$",
            message = USERNAME_ERROR_MESSAGE)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    String username;

    @Schema(name = "password", example = "Pass1234!", required = true)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$",
            message = PASSWORD_ERROR_MESSAGE)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    String password;

    public UserDto() {
    }

    public UserDto(String username) {
        this.username = username;
    }

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
