package com.virtualwallet.services.contracts;

import com.virtualwallet.models.CardType;
import com.virtualwallet.models.User;

import java.util.List;

public interface CardTypeService {
    CardType createCardType(User user, CardType cardType);

    void deleteCardType(int cardTypeId, User user);

    void updateCardType(CardType cardType, User user);

    CardType getCardType(int cardTypeId);

    List<CardType> getAllCardTypes();
}
