package com.virtualwallet.repositories;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardToWalletTransactionRepository;
import com.virtualwallet.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CardToWalletTransactionImpl extends AbstractCrudRepository<CardToWalletTransaction> implements CardToWalletTransactionRepository {

    private final UserRepository userRepository;

    @Autowired
    public CardToWalletTransactionImpl(SessionFactory sessionFactory, UserRepository userRepository) {
        super(CardToWalletTransaction.class, sessionFactory);
        this.userRepository = userRepository;
    }

    @Override
    public List<CardToWalletTransaction> getAllCardTransactionsWithFilter(User user,
                                                                          CardTransactionModelFilterOptions transactionFilter) {
        try (Session session = sessionFactory.openSession()) {

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            params.put("userId", user.getId());

            transactionFilter.getStartDate().ifPresent(startDate -> {
                filters.add("time >= :startDate");
                params.put("startDate", startDate);
            });

            transactionFilter.getEndDate().ifPresent(endDate -> {
                filters.add("time <= :endDate");
                params.put("endDate", endDate);
            });

            transactionFilter.getRecipient().ifPresent(value -> {
                if (!value.isBlank()) {
                    User user1;
                    int id;
                    try {
                        user1 = userRepository.getByStringField("username", value);
                       id = user1.getId();
                    }catch (EntityNotFoundException e){
                        id = -1;
                    }

                    filters.add("userId = :recipient");
                    params.put("recipient", id);
                }
            });

//            transactionFilter.getSender().ifPresent(value -> {
//                if (!value.isBlank()) {
//                    filters.add("sender like :sender");
//                    params.put("sender", String.format("%%%s%%", value));
//                }
//            });

            transactionFilter.getDirection().ifPresent(value -> {
                if (!value.isBlank()) {

                    int transactionTypeId = "Outgoing".equalsIgnoreCase(value) ? 2 : ("Incoming".equalsIgnoreCase(value) ? 1 : 0);
                    filters.add("transactionTypeId = :direction");
                    params.put("direction", transactionTypeId);

                }
            });

            StringBuilder queryString = new StringBuilder();

            queryString.append("From CardToWalletTransaction where userId = :userId ");

            if (!filters.isEmpty()) {

                queryString.append(" and ").append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(transactionFilter));

            Query<CardToWalletTransaction> query = session.createQuery(queryString.toString(), CardToWalletTransaction.class);
            query.setProperties(params);
            return query.list();
        }
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

    private String generateOrderBy(CardTransactionModelFilterOptions transactionModelFilterOptions) {

        if (transactionModelFilterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (transactionModelFilterOptions.getSortBy().get()) {
            case "amount":
                orderBy = "amount";
                break;
            case "time":
                orderBy = "time";
                break;
            default:
                orderBy = "time";
        }
        orderBy = String.format(" order by %s", orderBy);

        if (transactionModelFilterOptions.getSortOrder().isPresent() &&
                transactionModelFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}
