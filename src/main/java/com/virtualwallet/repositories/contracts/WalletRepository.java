package com.virtualwallet.repositories.contracts;


import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;

import java.util.List;

public interface WalletRepository {
    List<Wallet> getAll();
    Wallet getByStringField(String fieldName, String fieldValue);
    Wallet getById(int id);
    void create(Wallet wallet);
    void update(Wallet wallet);
    void delete(int id);

    List<Wallet> getAllWallets(User user);

    boolean checkWalletOwnership(int userId, int walletId);
}
