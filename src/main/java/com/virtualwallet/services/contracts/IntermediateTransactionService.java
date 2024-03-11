package com.virtualwallet.services.contracts;

import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;

import java.util.List;

public interface IntermediateTransactionService {
    List<WalletToWalletTransaction> getAllWithFilter(User user, WalletTransactionModelFilterOptions transactionFilter);

    void approveTransaction(User user, int transactionId);

    void cancelTransaction(User user, int transactionId);
}
