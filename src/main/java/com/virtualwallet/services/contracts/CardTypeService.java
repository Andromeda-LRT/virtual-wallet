package com.virtualwallet.services.contracts;

import com.virtualwallet.models.CardType;

import java.util.List;

public interface CardTypeService {
    void create(CardType entity);

    void update(CardType entity);

    void delete(int id);

    List<CardType> getAll();

    CardType getById(int id);
}
