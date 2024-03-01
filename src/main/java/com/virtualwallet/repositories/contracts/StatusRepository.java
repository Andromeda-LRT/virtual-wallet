package com.virtualwallet.repositories.contracts;

import com.virtualwallet.models.Status;

import java.util.List;

public interface StatusRepository {
    void create(Status entity);

    void update(Status entity);

    void delete(int id);

    List<Status> getAll();

    Status getById(int id);

    Status getByStringField(String fieldName, String fieldValue);
}
