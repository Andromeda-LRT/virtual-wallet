package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.models.*;

import java.util.List;

public interface CardTransactionService {
    List<CardToWalletTransaction> getAllCardTransactions();
    List<CardToWalletTransaction> getAllCardTransactionsWithFilter
            (User user, CardTransactionModelFilterOptions transactionFilter);
    List<CardToWalletTransaction> getUserCardTransactions(int walletId,
                                                          User user,
                                                          CardTransactionModelFilterOptions transactionFilter);
    CardToWalletTransaction getCardTransactionById(int cardTransactionId);
    void updateCardTransaction(CardToWalletTransaction cardTransaction, User user);
    void approveTransaction(CardToWalletTransaction cardTransaction, User user, Card card, Wallet wallet);
    void declineTransaction(CardToWalletTransaction cardTransaction, User user, Card card, Wallet wallet);

}
