package com.virtualwallet.models.model_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

public class UserDto {

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
    @Pattern(regexp = "^[0-9]+$",
            message = "Phone number must include only digits")
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 10, max = 10, message = INVALID_PHONE_NUMBER_ERROR_MESSAGE)
    String phoneNumber;

    public UserDto() {
    }

    public UserDto(String firstName, String lastName, String email,
                   String username, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
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
