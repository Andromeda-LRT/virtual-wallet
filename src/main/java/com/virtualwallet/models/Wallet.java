package com.virtualwallet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private int walletId;

    @Column(name = "name")
    private String name;
    @Column(name = "iban")
    private String iban;

    @Column(name = "balance")
    private double balance;

    @Column(name = "is_archived")
    private boolean isArchived;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    public Wallet(int walletId, String iban, double balance, boolean isArchived, String name, User createdBy) {
        this.walletId = walletId;
        this.iban = iban;
        this.balance = balance;
        this.isArchived = isArchived;
        this.name = name;
        this.createdBy = createdBy;
    }

    public Wallet() {
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
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

    public boolean isArchived() {
        return isArchived;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User userId) {
        this.createdBy = userId;
    }
}
