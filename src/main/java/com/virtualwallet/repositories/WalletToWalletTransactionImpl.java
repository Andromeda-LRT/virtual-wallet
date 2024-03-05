package com.virtualwallet.repositories;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
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

import java.util.List;
@Repository
public class WalletToWalletTransactionImpl extends AbstractCrudRepository<WalletToWalletTransaction> implements WalletToWalletTransactionRepository {

    @Autowired
    public WalletToWalletTransactionImpl(SessionFactory sessionFactory) {
        super(WalletToWalletTransaction.class, sessionFactory);
    }


    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter(User user, TransactionModelFilterOptions transactionFilter) {
        return null;
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

}
