package com.virtualwallet.repositories.contracts;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;
import jakarta.transaction.Transaction;

import java.util.List;

public interface WalletToWalletTransactionRepository {
    List<WalletToWalletTransaction> getAll();

    List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (User user, TransactionModelFilterOptions transactionFilter, Wallet wallet);

    List<WalletToWalletTransaction> getUserWalletTransactions(Wallet wallet);

    WalletToWalletTransaction getById(int walletTransactionId);

    void create(WalletToWalletTransaction transaction);

    void update(WalletToWalletTransaction transaction);
}
