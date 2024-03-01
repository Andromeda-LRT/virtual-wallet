package com.virtualwallet.services.contracts;

import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;

import java.util.List;

public interface WalletService {
    //todo delete method once transition to transactionwithCard is complete -- Ted
    //String addMoneyToWallet(User user, int card_id);

    List<Wallet> getAllWallets(User user);

    Wallet getWalletById(User user, int wallet_id);

    Wallet createWallet(User user, Wallet wallet);

    Wallet updateWallet(User user, Wallet wallet);

    void delete(User user, int wallet_id);

    List<WalletToWalletTransaction> getAllWalletTransactions(User user, int wallet_id);

    WalletToWalletTransaction getTransactionById(User user, int wallet_id, int transaction_id);

    void walletToWalletTransaction(User user, int wallet_from_id, WalletToWalletTransaction transaction);

//   Transaction updateTransaction(User user,Transaction transaction, int wallet_id);

    void approveTransaction(User user, int transaction_id, int wallet_id);

    void cancelTransaction(User user, int transaction_id, int wallet_id);

    void transactionWithCard(User user, int card_id, int wallet_id);
}
