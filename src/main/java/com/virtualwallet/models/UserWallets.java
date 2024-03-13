package com.virtualwallet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_wallets")
public class UserWallets {

    @EmbeddedId
    private UserWalletsId id;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id", updatable = false, insertable = false)
    private Wallet wallet;

    public UserWallets(User user, Wallet wallet) {
        this.id = new UserWalletsId(user.getId(),wallet.getWalletId());
        this.user = user;
        this.wallet = wallet;
    }

    public UserWallets() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
