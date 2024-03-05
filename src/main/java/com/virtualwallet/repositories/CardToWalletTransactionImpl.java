package com.virtualwallet.repositories;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardToWalletTransactionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class CardToWalletTransactionImpl extends AbstractCrudRepository<CardToWalletTransaction> implements CardToWalletTransactionRepository {


    @Autowired
    public CardToWalletTransactionImpl(SessionFactory sessionFactory) {
        super(CardToWalletTransaction.class, sessionFactory);
    }

    @Override
    public List<CardToWalletTransaction> getAllCardTransactionsWithFilter(User user, TransactionModelFilterOptions transactionFilter) {
        return null;
    }

    @Override
    public List<CardToWalletTransaction> getAllUserCardTransactions(int cardId) {
        try(Session session = sessionFactory.openSession()){
            Query<CardToWalletTransaction> query = session.createQuery("From CardToWalletTransaction where cardId = :cardId", CardToWalletTransaction.class);
            query.setParameter("cardId", cardId);
            List<CardToWalletTransaction> result = query.list();
            if (result.isEmpty()){
                throw new EntityNotFoundException("Card", "id", String.valueOf(cardId), "transactions");
            }
            return result;
        }
    }

    @Override
    public CardToWalletTransaction get(int cardTransactionId) {
        try (Session session = sessionFactory.openSession()) {
            CardToWalletTransaction cardToWalletTransaction
                    = session.get(CardToWalletTransaction.class, cardTransactionId);
            if (cardToWalletTransaction == null) {
                throw new EntityNotFoundException("Card transaction", "id", String.valueOf(cardTransactionId));
            }
            return cardToWalletTransaction;
        }
    }


}
