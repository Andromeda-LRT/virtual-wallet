package com.virtualwallet.repositories;

import com.virtualwallet.models.User;
import com.virtualwallet.models.UserWallets;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.repositories.contracts.WalletRepository;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WalletRepositoryImpl extends AbstractCrudRepository<Wallet> implements WalletRepository {
    @Autowired
    public WalletRepositoryImpl(SessionFactory sessionFactory) {
        super(Wallet.class, sessionFactory);
    }

    @Override
    public List<Wallet> getAllWallets(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery("from Wallet where createdBy = :user_id AND isArchived = false", Wallet.class);
            query.setParameter("user_id", user.getId());
            return query.list();
        }
    }

    @Override
    public boolean checkWalletOwnership(int userId, int walletId) {
        String sql = "SELECT 1 FROM user_wallets WHERE user_id = :userId AND wallet_id = :walletId";
        try (Session session = sessionFactory.openSession()) {
            Integer count = (Integer)  session.createNativeQuery(sql)
                    .setParameter("userId", userId)
                    .setParameter("walletId", walletId)
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
             return false;
        }
    }

    @Override
    public void addUserToWallet(UserWallets userWallets){
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(userWallets);
            session.getTransaction().commit();
        }
    }

    @Override
    public void removeUserFromWallet(UserWallets userWallets){
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(userWallets);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<User> getWalletUsers(int wallet_id){
        String sql = "SELECT u.* FROM users u " +
                "JOIN user_wallets uw ON u.user_id = uw.user_id " +
                "WHERE uw.wallet_id = :walletId";

        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(sql, User.class)
                    .setParameter("walletId", wallet_id)
                    .getResultList();
        }
    }
}
