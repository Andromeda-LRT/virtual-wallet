package com.virtualwallet.repositories;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.repositories.contracts.BaseReadRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


import java.util.List;

import static java.lang.String.format;


public abstract class AbstractReadRepository<T> implements BaseReadRepository<T> {
    private final Class<T> klas;

    protected final SessionFactory sessionFactory;

    public AbstractReadRepository(Class<T> klas, SessionFactory sessionFactory) {
        this.klas = klas;
        this.sessionFactory = sessionFactory;
    }

    /**
     * Retrieves an entity from the database that has a <code>field</code> equal to <code>value</code>.
     * <br/>
     * Example: <code>getByField("id, 1, User.class)</code>
     * will execute the following HQL: <code>from User where id = 1;</code>
     *
     * @param name  the name of the field
     * @param value the value of the field
     * @return an entity that matches the given criteria
     */

    @Override
    public <V> T getByField(String name, V value) {
        final String query = format("from %s where %s = :value", klas.getSimpleName(), name);
        final String notFoundErrorMessage = format("%s with %s %s not found", klas.getSimpleName(), name, value);

        try (Session session = sessionFactory.openSession()) {
            return session
                    .createQuery(query, klas)
                    .setParameter("value", value)
                    .uniqueResultOptional()
                    .orElseThrow(() -> new EntityNotFoundException(notFoundErrorMessage));
        }
    }

    @Override
    public T getById(int id) {
        return getByField("id", id);
    }

    @Override
    public T getByStringField(String fieldName, String fieldValue) {
        return getByField(fieldName, fieldValue);
    }

    @Override
    public List<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(format("from %s ", klas.getName()), klas).list();
        }
    }
}
