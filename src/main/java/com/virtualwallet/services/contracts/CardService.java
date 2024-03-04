package com.virtualwallet.services.contracts;

import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;

import java.util.List;

public interface CardService {
    Card createCard(User createdBy, Card card);

    void deleteCard(int card_id, User user);

    Card updateCard(Card card, User user);

    Card getCard(int card_id, User user);

    List<Card> getAllUserCards(User user);

    void verifyCardExistence(int cardId);

    void authorizeCardAccess(int card_id, User user);
}
