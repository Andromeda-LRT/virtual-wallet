package com.virtualwallet.models.response_model_dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

public class WalletUserDto {

    @NotEmpty(message = "Username can't be empty.")
    private String username;
    private String profilePicture;

    public WalletUserDto() {
    }

    public WalletUserDto(String username, String profilePicture) {
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WalletUserDto that)) return false;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
