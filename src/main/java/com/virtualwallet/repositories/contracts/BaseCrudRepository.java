package com.virtualwallet.repositories.contracts;

public interface BaseCrudRepository<T> extends BaseReadRepository<T> {

    void create(T entity);

    void update(T entity);

    void delete(int id);
}
