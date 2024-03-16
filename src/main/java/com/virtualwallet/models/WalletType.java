package com.virtualwallet.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "wallet_types")
public class WalletType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    int id;
    @Column(name = "type_name")
    String type;

    public WalletType() {
    }

    public WalletType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String roleName) {
        this.type = roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WalletType walletType)) return false;
        return getId() == walletType.getId() && Objects.equals(getType(), walletType.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType());
    }
}
