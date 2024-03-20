package com.virtualwallet.repositories.contracts;

import com.virtualwallet.models.CardType;

import java.util.List;

public interface CardTypeRepository {
    void create(CardType entity);

    void update(CardType entity);

    void delete(int id);

    List<CardType> getAll();

    CardType getById(int id);

    CardType getByStringField(String fieldName, String fieldValue);
}
