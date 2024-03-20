package com.virtualwallet.repositories.contracts;

import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;

import java.util.List;

public interface WalletToWalletTransactionRepository {
    List<WalletToWalletTransaction> getAll();

    List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (User user, WalletTransactionModelFilterOptions transactionFilter);

    List<WalletToWalletTransaction> getUserWalletTransactions(User user,
                                                              WalletTransactionModelFilterOptions transactionFilter,
                                                              int wallet_id);

    WalletToWalletTransaction getById(int walletTransactionId);

    void create(WalletToWalletTransaction transaction);

    void update(WalletToWalletTransaction transaction);
}
