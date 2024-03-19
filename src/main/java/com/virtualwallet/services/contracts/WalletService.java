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

    List<WalletToWalletTransaction> getUserWalletTransactions(WalletTransactionModelFilterOptions transactionFilter, User user, int wallet_id);

    List<CardToWalletTransaction> getUserCardTransactions(int walletId, User user, CardTransactionModelFilterOptions transactionFilter);

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
    //todo to potentially delete - Ted
//    boolean verifyIfUserIsPartOfWallet(User user, int wallet_id);

    /**
     *
     * @param user
     * @param wallet
     * @return returns a Boolean depending on whether the
     * current Wallet obj is created by provided User and does not throw exception
     */
    boolean verifyIfUserIsWalletOwner(User user, Wallet wallet);

    /**
     *
     * @param wallet
     * @param user
     * @return returns a Boolean depending on whether the
     * current Wallet obj is created by provided User
     * @throws  com.virtualwallet.exceptions.UnauthorizedOperationException if User is not owner of wallet
     */
    boolean verifyIfUserIsWalletOwner(Wallet wallet, User user);
    void checkWalletOwnership(User user, int walletId);
}
