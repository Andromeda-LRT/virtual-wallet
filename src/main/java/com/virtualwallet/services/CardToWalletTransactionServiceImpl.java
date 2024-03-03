package com.virtualwallet.services;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.services.contracts.CardTransactionService;
import com.virtualwallet.services.contracts.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

@Service
public class CardToWalletTransactionServiceImpl implements CardTransactionService {

    private final cardToWalletTransactionRepository cardTransactionRepository;
    private final StatusService statusService;
    @Autowired
    public CardToWalletTransactionServiceImpl(cardToWalletTransactionRepository cardTransactionRepository,
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
            (User user, TransactionModelFilterOptions transactionFilter) {

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
    public void approveTransaction(CardToWalletTransaction cardTransaction, User user, Card card) {
        cardTransaction.setCardId(card.getId());
        cardTransaction.setUserId(user.getId());
        cardTransaction.setTransactionTypeId(INCOMING_TRANSACTION_TYPE_ID);
        cardTransaction.setStatus(statusService.getStatus(CONFIRMED_TRANSACTION_ID));
        cardTransactionRepository.create(cardTransaction);
    }

    @Override
    public void declineTransaction(CardToWalletTransaction cardTransaction, User user, Card card) {
        cardTransaction.setCardId(card.getId());
        cardTransaction.setUserId(user.getId());
        cardTransaction.setTransactionTypeId(1);
        cardTransaction.setStatus(statusService.getStatus(DECLINED_TRANSACTION_ID));
        cardTransactionRepository.create(cardTransaction);
    }
}
