package com.virtualwallet.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "wallet_transactions")
public class WalletToWalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "transaction_type_id")
    private int transactionTypeId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "recipient_wallet_id")
    private int recipientWalletId;

    @Column(name = "wallet_id")
    private int walletId;

    @ManyToOne
    @JoinColumn(name = "status")
    private String status;

    public WalletToWalletTransaction(int transactionId, double amount, LocalDateTime time, int transactionTypeId, int userId, int recipientWalletId, int walletId, String status) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.time = time;
        this.transactionTypeId = transactionTypeId;
        this.userId = userId;
        this.recipientWalletId = recipientWalletId;
        this.walletId = walletId;
        this.status = status;
    }

    public WalletToWalletTransaction() {
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecipientWalletId() {
        return recipientWalletId;
    }

    public void setRecipientWalletId(int recipientWalletId) {
        this.recipientWalletId = recipientWalletId;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletToWalletTransaction that = (WalletToWalletTransaction) o;
        return transactionId == that.transactionId
                && Double.compare(amount, that.amount) == 0
                && transactionTypeId == that.transactionTypeId
                && userId == that.userId
                && recipientWalletId == that.recipientWalletId
                && walletId == that.walletId
                && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, amount, time, transactionTypeId, userId, recipientWalletId, walletId);
    }
}
