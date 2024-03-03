package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.models.*;

import java.util.List;

public interface WalletTransactionService {
    List<WalletToWalletTransaction> getAllWalletTransactions();
    List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (User user, TransactionModelFilterOptions transactionFilter);
    List<WalletToWalletTransaction> getUserWalletTransactions(Wallet wallet);
    WalletToWalletTransaction getWalletTransactionById(int walletTransactionId);
    boolean createWalletTransaction(User user, WalletToWalletTransaction transaction);
    void approveTransaction(WalletToWalletTransaction transaction);
    void cancelTransaction(WalletToWalletTransaction transaction);

}