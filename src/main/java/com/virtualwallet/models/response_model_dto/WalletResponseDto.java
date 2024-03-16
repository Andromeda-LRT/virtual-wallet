package com.virtualwallet.models.response_model_dto;

import com.virtualwallet.models.User;

import java.util.List;

public class WalletResponseDto {

    private int walletId;
    private String name;
    private String iban;
    private double balance;
    private String type;
    private List<User> walletUsers;

    public WalletResponseDto () {

    }

    public WalletResponseDto(int walletId, String name,
                             String iban, double balance,
                             String type, List<User> walletUsers) {
        this.walletId = walletId;
        this.name = name;
        this.iban = iban;
        this.balance = balance;
        this.type = type;
        this.walletUsers = walletUsers;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<User> getWalletUsers() {
        return walletUsers;
    }

    public void setWalletUsers(List<User> walletUsers) {
        this.walletUsers = walletUsers;
    }
}
