package com.virtualwallet.repositories.contracts;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;

import java.util.List;

public interface CardToWalletTransactionRepository {
    List<CardToWalletTransaction> getAll();

    List<CardToWalletTransaction> getAllCardTransactionsWithFilter
            (User user, TransactionModelFilterOptions transactionFilter);

    List<CardToWalletTransaction> getAllUserCardTransactions(int cardId);

    CardToWalletTransaction get(int cardTransactionId);

    void update(CardToWalletTransaction cardTransaction);

    void create(CardToWalletTransaction cardTransaction);
}