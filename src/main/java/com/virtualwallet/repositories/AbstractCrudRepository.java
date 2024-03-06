package com.virtualwallet.repositories;

import com.virtualwallet.repositories.contracts.BaseCrudRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


public abstract class AbstractCrudRepository<T> extends AbstractReadRepository<T> implements BaseCrudRepository<T> {
    //todo have all other repoes extend abstractCrudRepo,
    // when extending apply the relevant class the repo will be working with
    // E.g. if its User repo --> <User> as type
    public AbstractCrudRepository(Class<T> klas, SessionFactory sessionFactory) {
        super(klas, sessionFactory);
    }

    @Override
    public void create(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        T objToDelete = getById(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(objToDelete);
            session.getTransaction().commit();
            //TODO implement soft delete
        }
    }

}
