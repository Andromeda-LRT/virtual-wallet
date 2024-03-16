package com.virtualwallet.services.contracts;

import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletType;

import java.util.List;

public interface WalletTypeService {
    WalletType createWalletType(User user, WalletType walletType);
    void deleteWalletType(int walletTypeId, User user);
    void updateWalletType(WalletType walletType, User user);
    WalletType getWalletType(int walletTypeId);
    List<WalletType> getAllWalletTypes();
}
