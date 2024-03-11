package com.virtualwallet.models.mvc_input_model_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;
import static com.virtualwallet.model_helpers.ModelConstantHelper.EMPTY_ERROR_MESSAGE;

public class RegisterDto extends LoginDto {

    @Schema(name = "firstName", example = "Ivan", required = true)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    String firstName;

    @Schema(name = "lastName", example = "Ivanov", required = true)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    String lastName;

    @NotEmpty(message = "Password confirmation can't be empty.")
    private String passwordConfirm;
    @Email(
            message = INVALID_EMAIL_ERROR_MESSAGE,
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    @NotEmpty(message = "Email can't be empty.")
    private String email;

    @Pattern(regexp = "^[0-9]+$",
            message = "Phone number must include only digits")
    @Size(min = 10, max = 10, message = INVALID_PHONE_NUMBER_ERROR_MESSAGE)
    String phoneNumber;

    public RegisterDto() {
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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
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
