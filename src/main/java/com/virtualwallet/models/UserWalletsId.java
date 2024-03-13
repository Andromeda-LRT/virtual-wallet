package com.virtualwallet.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class UserWalletsId implements Serializable {
    @Column(name = "user_id")
    private int userId;

    @Column(name = "wallet_id")
    private int walletId;

    public UserWalletsId(int userId, int walletId) {
        this.userId = userId;
        this.walletId = walletId;
    }

    public UserWalletsId() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }
}
