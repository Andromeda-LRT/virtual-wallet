package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;

import java.util.List;

public interface CardTransactionService {
    List<CardToWalletTransaction> getAllCardTransactions();
    List<CardToWalletTransaction> getAllCardTransactionsWithFilter
            (User user, TransactionModelFilterOptions transactionFilter);
    List<CardToWalletTransaction> getUserCardTransactions(Card card);
    CardToWalletTransaction getCardTransactionById(int cardTransactionId);
    void updateCardTransaction(CardToWalletTransaction cardTransaction, User user);
    void approveTransaction(CardToWalletTransaction cardTransaction, User user, Card card);
    void declineTransaction(CardToWalletTransaction cardTransaction, User user, Card card);

}
