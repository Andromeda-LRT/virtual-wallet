package com.virtualwallet.repositories;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.repositories.contracts.WalletToWalletTransactionRepository;
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
public class WalletToWalletTransactionImpl extends AbstractCrudRepository<WalletToWalletTransaction> implements WalletToWalletTransactionRepository {

    @Autowired
    public WalletToWalletTransactionImpl(SessionFactory sessionFactory) {
        super(WalletToWalletTransaction.class, sessionFactory);
    }

    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter(User user, TransactionModelFilterOptions transactionFilter, Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            params.put("walletId", wallet.getWalletId());

            transactionFilter.getStartDate().ifPresent(value -> {
                if (!value.toString().isBlank()) {
                    filters.add("startDate > :startDate");
                    params.put("startDate", String.format("%%%s%%", value));
                }
            });

            transactionFilter.getEndDate().ifPresent(value -> {
                if (!value.toString().isBlank()) {
                    filters.add("endDate < :endDate");
                    params.put("endDate", String.format("%%%s%%", value));
                }
            });

            transactionFilter.getRecipient().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("recipient like :recipient");
                    params.put("recipient", String.format("%%%s%%", value));
                }
            });

            transactionFilter.getSender().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("sender like :sender");
                    params.put("sender", String.format("%%%s%%", value));
                }
            });

            transactionFilter.getDirection().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("direction like :direction");
                    params.put("direction", String.format("%%%s%%", value));
                }
            });

            StringBuilder queryString = new StringBuilder();

            queryString.append("From WalletToWalletTransaction where walletId = :walletId ");

            if (!filters.isEmpty()) {
                queryString
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(transactionFilter));

            Query<WalletToWalletTransaction> query = session.createQuery(queryString.toString(), WalletToWalletTransaction.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public List<WalletToWalletTransaction> getUserWalletTransactions(Wallet wallet) {
        try(Session session = sessionFactory.openSession()){
            Query<WalletToWalletTransaction> query = session.createQuery("From WalletToWalletTransaction where walletId = :walletId", WalletToWalletTransaction.class);
            query.setParameter("walletId", wallet.getWalletId());
            List<WalletToWalletTransaction> result = query.list();
            if (result.isEmpty()){
                throw new EntityNotFoundException("Wallet", "id", String.valueOf(wallet.getWalletId()), "transactions");
            }
            return result;
        }
    }

    @Override
    public WalletToWalletTransaction getById(int walletTransactionId) {
        try (Session session = sessionFactory.openSession()) {
            WalletToWalletTransaction walletToWalletTransaction
                    = session.get(WalletToWalletTransaction.class, walletTransactionId);
            if (walletToWalletTransaction == null) {
                throw new EntityNotFoundException("Wallet transaction", "id", String.valueOf(walletTransactionId));
            }
            return walletToWalletTransaction;
        }
    }

    private String generateOrderBy(TransactionModelFilterOptions transactionModelFilterOptions) {

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
