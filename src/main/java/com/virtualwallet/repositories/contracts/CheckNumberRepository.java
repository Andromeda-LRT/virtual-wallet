package com.virtualwallet.repositories.contracts;

import com.virtualwallet.models.CheckNumber;

import java.util.List;

public interface CheckNumberRepository {
    void create(CheckNumber entity);

    void update(CheckNumber entity);

    void delete(int id);

    List<CheckNumber> getAll();

    CheckNumber getById(int id);

    CheckNumber getByNumber(String fieldValue);
}
