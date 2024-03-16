package com.virtualwallet.repositories.contracts;

import com.virtualwallet.models.WalletType;

import java.util.List;

public interface WalletTypeRepository {
    void create(WalletType entity);

    void update(WalletType entity);

    void delete(int id);

    List<WalletType> getAll();

    WalletType getById(int id);

    WalletType getByStringField(String fieldName, String fieldValue);
}
