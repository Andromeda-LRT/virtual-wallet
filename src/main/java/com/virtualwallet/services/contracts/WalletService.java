package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.*;

import java.util.List;

public interface WalletService {

    List<Wallet> getAllWallets(User user);

    List<Wallet> getAllPersonalWallets(User user);

    List<Wallet> getAllJoinWallets(User user);

    List<User> getRecipient(UserModelFilterOptions userFilter);

    Wallet getWalletById(User user, int wallet_id);

    Wallet createWallet(User user, Wallet wallet);

    Wallet updateWallet(User user, Wallet wallet);

    void delete(User user, int wallet_id);

    List<WalletToWalletTransaction> getAllWalletTransactions(User user, int wallet_id);

    List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter(WalletTransactionModelFilterOptions transactionFilter, User user, int wallet_id);

    List<CardToWalletTransaction> getAllCardTransactions(User user, int card_id);

    List<CardToWalletTransaction> getAllCardTransactionsWithFilter(User user, CardTransactionModelFilterOptions transactionFilter);

    WalletToWalletTransaction getTransactionById(User user, int wallet_id, int transaction_id);

    void walletToWalletTransaction(User user, int wallet_from_id, WalletToWalletTransaction transaction);

//   Transaction updateTransaction(User user,Transaction transaction, int wallet_id);

    Wallet checkIbanExistence(String ibanTo);

    CardToWalletTransaction transactionWithCard(User user, int card_id, int wallet_id,
                                                CardToWalletTransaction cardTransaction);

    Wallet getByStringField(String id, String s);

    void checkWalletBalance(Wallet wallet, double amount);

    void chargeWallet(Wallet wallet, double amount);

    void transferMoneyToRecipientWallet(Wallet recipientWallet, double amount);

    void addUserToWallet(User user, int wallet_id, int user_id);

    void removeUserFromWallet(User user, int wallet_id, int user_id);

    List<User> getWalletUsers(User user, int wallet_id);
}
