package com.virtualwallet.repositories;

import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.repositories.contracts.WalletRepository;
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

import static com.virtualwallet.model_helpers.ModelConstantHelper.INCOMING_TRANSACTION;
import static com.virtualwallet.model_helpers.ModelConstantHelper.OUTGOING_TRANSACTION;

@Repository
public class WalletToWalletTransactionImpl extends AbstractCrudRepository<WalletToWalletTransaction>
        implements WalletToWalletTransactionRepository {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletToWalletTransactionImpl(SessionFactory sessionFactory, WalletRepository repository) {
        super(WalletToWalletTransaction.class, sessionFactory);
        this.walletRepository = repository;
    }

    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (User user, WalletTransactionModelFilterOptions transactionFilter) {
        try (Session session = sessionFactory.openSession()) {

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();


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
                    Wallet wallet1;
                    int id;
                    try{
                        wallet1 = walletRepository.getByStringField("iban", value);
                        id = wallet1.getWalletId();
                    }catch (EntityNotFoundException e){
                        id = -1;
                    }
                    filters.add("recipientWalletId = :recipient");
                    params.put("recipient", id);
                }
            });

            transactionFilter.getSender().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("sender.username like :sender");
                    params.put("sender", String.format("%%%s%%", value));
                }
            });

            transactionFilter.getDirection().ifPresent(value -> {
                if (!value.isBlank()) {

                    int transactionTypeId = OUTGOING_TRANSACTION.equalsIgnoreCase(value)
                            ? 2 : (INCOMING_TRANSACTION.equalsIgnoreCase(value) ? 1 : 0);
                    filters.add("transactionTypeId = :direction");
                    params.put("direction", transactionTypeId);

                }
            });

            StringBuilder queryString = new StringBuilder();

            queryString.append("From WalletToWalletTransaction");

            if (!filters.isEmpty()) {

                queryString.append(" where ").append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(transactionFilter));

            Query<WalletToWalletTransaction> query = session.createQuery(queryString.toString(),
                    WalletToWalletTransaction.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public List<WalletToWalletTransaction> getUserWalletTransactions
            (User user, WalletTransactionModelFilterOptions transactionFilter,int wallet_id) {
        try (Session session = sessionFactory.openSession()) {

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            params.put("walletId", wallet_id);

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
                    Wallet wallet1;
                    int id;
                    try {
                        wallet1 = walletRepository.getByStringField("iban", value);
                        id = wallet1.getWalletId();
                    } catch (EntityNotFoundException e) {
                        id = -1;
                    }
                    filters.add("recipientWalletId = :recipient");
                    params.put("recipient", id);
                }
            });

            transactionFilter.getSender().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("sender.username like :sender");
                    params.put("sender", String.format("%%%s%%", value));
                }
            });

            transactionFilter.getDirection().ifPresent(value -> {
                if (!value.isBlank()) {

                    int transactionTypeId = OUTGOING_TRANSACTION.equalsIgnoreCase(value)
                            ? 2 : (INCOMING_TRANSACTION.equalsIgnoreCase(value) ? 1 : 0);
                    filters.add("transactionTypeId = :direction");
                    params.put("direction", transactionTypeId);

                }
            });

            StringBuilder queryString = new StringBuilder();

            queryString.append("From WalletToWalletTransaction where walletId = :walletId ");

            if (!filters.isEmpty()) {

                queryString.append(" and ").append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(transactionFilter));

            Query<WalletToWalletTransaction> query = session.createQuery(queryString.toString(),
                    WalletToWalletTransaction.class);
            query.setProperties(params);
            return query.list();
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

    private String generateOrderBy(WalletTransactionModelFilterOptions transactionModelFilterOptions) {

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
