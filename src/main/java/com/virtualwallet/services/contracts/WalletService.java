package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.models.*;

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

    List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter(TransactionModelFilterOptions transactionFilter, User user, int wallet_id);

    List<CardToWalletTransaction> getAllCardTransactions(User user, int card_id);

    WalletToWalletTransaction getTransactionById(User user, int wallet_id, int transaction_id);

    void walletToWalletTransaction(User user, int wallet_from_id, WalletToWalletTransaction transaction);

//   Transaction updateTransaction(User user,Transaction transaction, int wallet_id);

    Wallet checkIbanExistence(String ibanTo);

    void approveTransaction(User user, int transaction_id, int wallet_id);

    void cancelTransaction(User user, int transaction_id, int wallet_id);

    CardToWalletTransaction transactionWithCard(User user, int card_id, int wallet_id,
                                                CardToWalletTransaction cardTransaction);
}
