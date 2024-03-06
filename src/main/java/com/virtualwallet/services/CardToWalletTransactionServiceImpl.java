package com.virtualwallet.services;

import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.*;
import com.virtualwallet.repositories.contracts.CardToWalletTransactionRepository;
import com.virtualwallet.services.contracts.CardTransactionService;
import com.virtualwallet.services.contracts.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

@Service
public class CardToWalletTransactionServiceImpl implements CardTransactionService {

    private final CardToWalletTransactionRepository cardTransactionRepository;
    private final StatusService statusService;
    @Autowired
    public CardToWalletTransactionServiceImpl(CardToWalletTransactionRepository cardTransactionRepository,
                                              StatusService statusService) {
        this.cardTransactionRepository = cardTransactionRepository;
        this.statusService = statusService;
    }

    @Override
    public List<CardToWalletTransaction> getAllCardTransactions() {
        return cardTransactionRepository.getAll();
    }

    @Override
    public List<CardToWalletTransaction> getAllCardTransactionsWithFilter
            (User user, WalletTransactionModelFilterOptions transactionFilter) {

        return cardTransactionRepository.getAllCardTransactionsWithFilter(user, transactionFilter);
    }

    @Override
    public List<CardToWalletTransaction> getUserCardTransactions(Card card) {
        return cardTransactionRepository.getAllUserCardTransactions(card.getId());
    }

    @Override
    public CardToWalletTransaction getCardTransactionById(int cardTransactionId) {
        return cardTransactionRepository.get(cardTransactionId);
    }

    @Override
    public void updateCardTransaction(CardToWalletTransaction cardTransaction, User user) {
        cardTransactionRepository.update(cardTransaction);
    }

    @Override
    public void approveTransaction(CardToWalletTransaction cardTransaction,
                                   User user, Card card, Wallet wallet) {
        populateCardTransactionDetails(cardTransaction, user, card, statusService
                .getStatus(CONFIRMED_TRANSACTION_ID));
        cardTransactionRepository.create(cardTransaction);
        wallet.getCardTransactions().add(cardTransaction);
    }

    @Override
    public void declineTransaction(CardToWalletTransaction cardTransaction,
                                   User user, Card card, Wallet wallet) {
        populateCardTransactionDetails(cardTransaction, user, card, statusService
                .getStatus(DECLINED_TRANSACTION_ID));
        cardTransactionRepository.create(cardTransaction);
        wallet.getCardTransactions().add(cardTransaction);
    }

    private void populateCardTransactionDetails(CardToWalletTransaction cardTransaction,
                                                User user, Card card, Status status) {
        cardTransaction.setCardId(card.getId());
        cardTransaction.setUserId(user.getId());
        cardTransaction.setTransactionTypeId(INCOMING_TRANSACTION_TYPE_ID);
        cardTransaction.setStatus(status);
    }
}
