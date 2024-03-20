package com.virtualwallet.models.mvc_input_model_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;
import static com.virtualwallet.model_helpers.ModelConstantHelper.EMPTY_ERROR_MESSAGE;

public class RegisterDto {

    @NotEmpty(message = "Username can't be empty.")
    @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$",
            message = USERNAME_ERROR_MESSAGE)
    private String username;
    @NotEmpty(message = "Password can't be empty.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{4,20}$",
            message = PASSWORD_ERROR_MESSAGE)
    private String password;

    @Size(min = 3, max = 50, message = "Invalid length for first name.")
    @Schema(name = "firstName", example = "Ivan", required = true)
    @NotBlank(message = EMPTY_ERROR_MESSAGE)
    String firstName;
    @Size(min = 3, max = 50, message = "Invalid length for last name.")
    @Schema(name = "lastName", example = "Ivanov", required = true)
    @NotBlank(message = EMPTY_ERROR_MESSAGE)
    String lastName;

    @NotBlank(message = "Password confirmation can't be blank.")
    private String passwordConfirm;
    @Email(
            message = INVALID_EMAIL_ERROR_MESSAGE,
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    @Size(min = 8, max = 50, message = "Email must be at least 8 charactes long.")
    @NotBlank(message = "Email can't be empty.")
    private String email;

    @Pattern(regexp = "^[0-9]+$", message = "Phone number must include only digits")
    @Size(min = 10, max = 10, message = INVALID_PHONE_NUMBER_ERROR_MESSAGE)
    String phoneNumber;

    public RegisterDto() {
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
