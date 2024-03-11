package com.virtualwallet.models.input_model_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

public class UpdateUserDto {
    @Schema(name = "firstName", example = "Ivan", required = true)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 3, max = 20, message = NAME_ERROR_MESSAGE)
    String firstName;

    @Schema(name = "lastName", example = "Ivanov", required = true)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 3, max = 20, message = NAME_ERROR_MESSAGE)
    String lastName;

    @Schema(name = "email", example = "email@email.com", required = true)
    @Email(
            message = INVALID_EMAIL_ERROR_MESSAGE,
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    String email;

    @Schema(name = "password", example = "Pass1234!", required = true)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$",
            message = PASSWORD_ERROR_MESSAGE)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    String password;

    @Pattern(regexp = "^[0-9]+$",
            message = "Phone number must include only digits")
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 10, max = 10, message = INVALID_PHONE_NUMBER_ERROR_MESSAGE)
    String phoneNumber;

    public UpdateUserDto() {
    }

    public UpdateUserDto(String firstName, String lastName, String email, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
