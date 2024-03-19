package com.virtualwallet.services;

import com.virtualwallet.exceptions.InsufficientFundsException;
import com.virtualwallet.exceptions.InvalidOperationException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.services.contracts.CardTransactionService;
import com.virtualwallet.services.contracts.IntermediateTransactionService;
import com.virtualwallet.services.contracts.WalletService;
import com.virtualwallet.services.contracts.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

@Service
public class IntermediateTransactionServiceImpl implements IntermediateTransactionService {

    private final WalletService walletService;
    private final CardTransactionService cardTransactionService;
    private final WalletTransactionService walletTransactionService;

    @Autowired
    public IntermediateTransactionServiceImpl(WalletService walletService,
                                              CardTransactionService cardTransactionService,
                                              WalletTransactionService walletTransactionService) {
        this.walletService = walletService;
        this.cardTransactionService = cardTransactionService;
        this.walletTransactionService = walletTransactionService;
    }

    @Override
    public List<WalletToWalletTransaction> getAllWithFilter
            (User user, WalletTransactionModelFilterOptions transactionFilter) {
        checkIfAdmin(user);
        return walletTransactionService.getAllWalletTransactionsWithFilter(user,transactionFilter);
    }

    @Override
    public List<CardToWalletTransaction> getAllCardTransactionsWithFilter
            (User user, CardTransactionModelFilterOptions cardTransactionFilter) {
        checkIfAdmin(user);
        return cardTransactionService.getAllCardTransactionsWithFilter(user, cardTransactionFilter);
    }

    //todo debug approveTransaction
    @Override
    public void approveTransaction(User user, int transactionId) {
        WalletToWalletTransaction transaction =
                walletTransactionService.getWalletTransactionById(transactionId);
        validateTransactionStatus(transaction);
        Wallet senderWallet;
        Wallet recipientWallet;
        try {
            senderWallet = walletService.getWalletById(user, transaction.getWalletId());
            recipientWallet = walletService.getWalletById(user, transaction.getRecipientWalletId());
            walletService.checkWalletBalance(senderWallet, transaction.getAmount());
            if (senderWallet.equals(recipientWallet)) {
                walletTransactionService.approveTransaction(transaction, recipientWallet);
                walletService.transferMoneyToRecipientWallet(senderWallet, transaction.getAmount());
                walletService.chargeWallet(senderWallet, transaction.getAmount());
            } else {
                walletTransactionService.approveTransaction(transaction, recipientWallet);
                walletService.transferMoneyToRecipientWallet(recipientWallet, transaction.getAmount());
                walletService.chargeWallet(senderWallet, transaction.getAmount());
            }
        } catch (InsufficientFundsException e) {
            walletTransactionService.cancelTransaction(transaction);
        }
    }

    @Override
    public void cancelTransaction(User user, int transactionId) {
        WalletToWalletTransaction transaction =
                walletTransactionService.getWalletTransactionById(transactionId);
        validateTransactionStatus(transaction);

        walletTransactionService.cancelTransaction(transaction);
    }

    private void validateTransactionStatus(WalletToWalletTransaction transaction) {
        if (!transaction.getStatus().getName().equals(TRANSACTION_PENDING_STATUS)) {
            throw new InvalidOperationException(PROCESSED_TRANSACTION_MESSAGE);
        }
    }

    private void checkIfAdmin(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }
}
