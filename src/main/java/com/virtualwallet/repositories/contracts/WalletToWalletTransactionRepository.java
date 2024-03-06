package com.virtualwallet.repositories.contracts;

import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;

import java.util.List;

public interface WalletToWalletTransactionRepository {
    List<WalletToWalletTransaction> getAll();

    List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (User user, WalletTransactionModelFilterOptions transactionFilter, Wallet wallet);

    List<WalletToWalletTransaction> getUserWalletTransactions(Wallet wallet);

    WalletToWalletTransaction getById(int walletTransactionId);

    void create(WalletToWalletTransaction transaction);

    void update(WalletToWalletTransaction transaction);
}
