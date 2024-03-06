package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.*;

import java.util.List;

public interface WalletTransactionService {
    List<WalletToWalletTransaction> getAllWalletTransactions();
    List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (User user, WalletTransactionModelFilterOptions transactionFilter, Wallet wallet);
    List<WalletToWalletTransaction> getUserWalletTransactions(Wallet wallet);
    WalletToWalletTransaction getWalletTransactionById(int walletTransactionId);
    boolean createWalletTransaction(User user, WalletToWalletTransaction transaction,
                                    Wallet senderWallet, Wallet recipientWallet);
    void approveTransaction(WalletToWalletTransaction transaction, Wallet recipientWallet);
    void cancelTransaction(WalletToWalletTransaction transaction);

}
