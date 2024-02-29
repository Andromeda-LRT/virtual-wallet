package com.virtualwallet.services.contracts;

import com.virtualwallet.models.Transaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;

import java.util.List;

public interface WalletService {
    List<Wallet> getAllWallets(User user, int user_id);

    Wallet getWalletById(User user, int user_id, int wallet_id);

    Wallet createWallet(User user, Wallet wallet);

    Wallet updateWallet(User user, Wallet wallet);

    void delete(User user,int user_id, int wallet_id);

    List<Transaction> getAllTransactions(User user,int user_id, int wallet_id);

    Transaction getTransactionById(User user, int wallet_id, int transaction_id);

    Transaction createTransaction(User user, Transaction transaction, int wallet_id);

   Transaction updateTransaction(User user,Transaction transaction, int wallet_id);

    void approveTransaction(User user, int transaction_id, int wallet_id);

    void cancelTransaction(User user, int transaction_id, int wallet_id);

    void transactionWithCard(User user, int card_id, int wallet_id);
}
