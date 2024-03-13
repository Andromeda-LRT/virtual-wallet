package com.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;
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

    @Column(name = "created_by")
    private int createdBy;

    @Column(name = "wallet_type_id")
    private int walletTypeId;
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

    public Wallet(int walletId, String iban, double balance, boolean isArchived, String name, int createdBy, int walletTypeId) {
        this.walletId = walletId;
        this.iban = iban;
        this.balance = balance;
        this.isArchived = isArchived;
        this.name = name;
        this.createdBy = createdBy;
        this.walletTypeId = walletTypeId;
    }

    public Wallet() {
    }

    public int getWalletTypeId() {
        return walletTypeId;
    }

    public void setWalletTypeId(int walletTypeId) {
        this.walletTypeId = walletTypeId;
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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int userId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wallet wallet)) return false;
        return walletId == wallet.walletId && Double.compare(balance, wallet.balance) == 0
                && isArchived == wallet.isArchived
                && createdBy == wallet.createdBy
                && Objects.equals(name, wallet.name)
                && Objects.equals(iban, wallet.iban)
                && Objects.equals(walletTransactions, wallet.walletTransactions)
                && Objects.equals(cardTransactions, wallet.cardTransactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId, name, iban, balance, isArchived, createdBy, walletTransactions, cardTransactions);
    }
}
