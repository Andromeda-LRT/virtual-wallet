package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;

import java.util.List;

public interface IntermediateTransactionService {
    List<WalletToWalletTransaction> getAllWithFilter
            (User user, WalletTransactionModelFilterOptions walletTransactionFilter);

    List<CardToWalletTransaction> getAllCardTransactionsWithFilter
            (User user, CardTransactionModelFilterOptions cardTransactionFilter);

    void approveTransaction(User user, int transactionId);

    void cancelTransaction(User user, int transactionId);
}
