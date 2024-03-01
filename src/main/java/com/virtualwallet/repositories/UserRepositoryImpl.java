package com.virtualwallet.repositories;

import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends AbstractCrudRepository<User> implements UserRepository {

    @Autowired
    public UserRepositoryImpl(Class<User> klas, SessionFactory sessionFactory) {
        super(klas, sessionFactory);
    }

    @Override
    public void blockUser(int user_id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "update User set isBlocked = true where id = :user_id";
            Query query = session.createQuery(hql);
            query.setParameter("user_id", user_id);
            query.executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Override
    public void unblockUser(int user_id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "update User set isBlocked = false where id = :user_id";
            Query query = session.createQuery(hql);
            query.setParameter("user_id", user_id);
            query.executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Override
    public void giveUserAdminRights(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String roleHql = "select r.id from Role r where r.name = :roleName";
            Query roleQuery = session.createQuery(roleHql);
            roleQuery.setParameter("roleName", "admin");
            Integer adminRoleId = (Integer) roleQuery.uniqueResult();


            String userSql = "UPDATE Users SET roleId = :adminRoleId WHERE userId = :userId";
            Query userQuery = session.createNativeQuery(userSql);
            userQuery.setParameter("adminRoleId", adminRoleId);
            userQuery.setParameter("userId", user.getId());
            userQuery.executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public void removeUserAdminRights(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String roleHql = "select r.id from Role r where r.name = :roleName";
            Query roleQuery = session.createQuery(roleHql);
            roleQuery.setParameter("roleName", "user");
            Integer userRoleId = (Integer) roleQuery.uniqueResult();


            String userSql = "UPDATE Users SET roleId = :adminRoleId WHERE userId = :userId";
            Query userQuery = session.createNativeQuery(userSql);
            userQuery.setParameter("userRoleId", userRoleId);
            userQuery.setParameter("userId", user.getId());
            userQuery.executeUpdate();
            session.getTransaction().commit();
        }
    }
}
