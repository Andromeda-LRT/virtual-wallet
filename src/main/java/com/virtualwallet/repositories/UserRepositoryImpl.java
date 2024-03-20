package com.virtualwallet.repositories;

import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.User;
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
public class UserRepositoryImpl extends AbstractCrudRepository<User> implements UserRepository {

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }

    @Override
    public List<User> getAllWithFilter(UserModelFilterOptions userFilter) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            populateFilterAndParams(userFilter, filters, params);

            StringBuilder queryString = new StringBuilder();

            queryString.append("from User");

            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(userFilter));

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }
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


            String userSql = "UPDATE Users SET role_id = :adminRoleId WHERE user_id = :userId";
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


            String userSql = "UPDATE Users SET role_id = :userRoleId WHERE user_id = :userId";
            Query userQuery = session.createNativeQuery(userSql);
            userQuery.setParameter("userRoleId", userRoleId);
            userQuery.setParameter("userId", user.getId());
            userQuery.executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void populateFilterAndParams(UserModelFilterOptions userFilter,
                                         List<String> filters,
                                         Map<String, Object> params) {
        userFilter.getPhoneNumber().ifPresent(value -> {
            if (!value.isBlank()) {
                filters.add("phoneNumber like :phoneNumber");
                params.put("phoneNumber", String.format("%%%s%%", value));
            }
        });

        userFilter.getUsername().ifPresent(value -> {
            if (!value.isBlank()) {
                filters.add("username like :username");
                params.put("username", String.format("%%%s%%", value));
            }
        });

        userFilter.getEmail().ifPresent(value -> {
            if (!value.isBlank()) {
                filters.add("email like :email");
                params.put("email", String.format("%%%s%%", value));
            }
        });
    }

    private String generateOrderBy(UserModelFilterOptions userFilter) {

        if (userFilter.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (userFilter.getSortBy().get()) {
            case "phoneNumber":
                orderBy = "phoneNumber";
                break;
            case "username":
                orderBy = "username";
                break;
            case "email":
                orderBy = "email";
                break;
            default:
                orderBy = "id";
        }
        orderBy = String.format(" order by %s", orderBy);

        if (userFilter.getSortOrder().isPresent() &&
                userFilter.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}
