package com.virtualwallet.model_helpers;

import java.util.Optional;

public class UserModelFilterOptions {

    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public UserModelFilterOptions(String username,
                                  String email,
                                  String phoneNumber,
                                  String sortBy,
                                  String sortOrder) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.phoneNumber = Optional.ofNullable(phoneNumber);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public UserModelFilterOptions(String username,
                                  String email,
                                  String firstName) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.phoneNumber = Optional.ofNullable(firstName);
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(Optional<String> sortBy) {
        this.sortBy = sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Optional<String> sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public void setUsername(Optional<String> username) {
        this.username = username;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public void setEmail(Optional<String> email) {
        this.email = email;
    }

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Optional<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
