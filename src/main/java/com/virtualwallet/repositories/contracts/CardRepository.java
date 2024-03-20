package com.virtualwallet.repositories.contracts;

import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;

import java.util.List;

public interface CardRepository {

    Card getById(int id);

    Card getByStringField(String fieldName, String fieldValue);

    List<Card> getAll();

    void create(Card card);

    void delete(int id);

    void update(Card card);

    Card getUserCard(User user, int cardId);

}
