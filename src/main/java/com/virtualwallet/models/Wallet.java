package com.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

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
    @JoinColumn(name = "created_by")
    private User createdBy;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name ="wallet_transaction_histories",
            joinColumns = @JoinColumn(name = "wallet_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    private Set<WalletToWalletTransaction> walletTransactions;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "card_transaction_histories",
            joinColumns = @JoinColumn(name = "wallet_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    private Set<CardToWalletTransaction> cardTransactions;

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

    public Set<WalletToWalletTransaction> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(Set<WalletToWalletTransaction> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }

    public Set<CardToWalletTransaction> getCardTransactions() {
        return cardTransactions;
    }

    public void setCardTransactions(Set<CardToWalletTransaction> cardTransactions) {
        this.cardTransactions = cardTransactions;
    }
}
