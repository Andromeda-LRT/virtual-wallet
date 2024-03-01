package com.virtualwallet.models.model_dto;

public class UserModelFilterDto {
    private String username;
    private String email;
    private String phoneNumber;
    private String sortBy;
    private String sortOrder;

    public UserModelFilterDto() {
    }

    public UserModelFilterDto(String username,
                              String email,
                              String phoneNumber,
                              String sortBy,
                              String sortOrder) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setPhoneNumber(String firstName) {
        this.phoneNumber = firstName;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
