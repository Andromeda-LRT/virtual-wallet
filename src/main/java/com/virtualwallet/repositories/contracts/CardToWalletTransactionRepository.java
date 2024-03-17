package com.virtualwallet.repositories.contracts;

import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;

import java.util.List;

public interface CardToWalletTransactionRepository {
    List<CardToWalletTransaction> getAll();

    List<CardToWalletTransaction> getAllCardTransactionsWithFilter
            (User user, CardTransactionModelFilterOptions transactionFilter);

    List<CardToWalletTransaction> getAllUserCardTransactions(int walletId, User user, CardTransactionModelFilterOptions transactionFilter);

    CardToWalletTransaction get(int cardTransactionId);

    void update(CardToWalletTransaction cardTransaction);

    void create(CardToWalletTransaction cardTransaction);
}
