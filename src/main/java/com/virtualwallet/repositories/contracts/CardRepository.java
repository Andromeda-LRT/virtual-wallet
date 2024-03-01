package com.virtualwallet.repositories.contracts;

import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;

import java.util.List;

public interface CardRepository {

    Card getById(int id);

    Card getByStringField(String fieldName, String fieldValue);

    List<Card> getAll();

    List<Card> getAllUserCards(int userId);

    void create(Card card);

    void delete(int id);

    void update(Card card);

    void addCardToUser(int userId, int cardId);

    void removeCardFromUser(int userId, int cardId);

    Card getUserCard(User user, int cardId);

}
