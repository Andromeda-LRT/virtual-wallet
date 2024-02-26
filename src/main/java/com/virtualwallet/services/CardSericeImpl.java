package com.virtualwallet.services;

import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.services.contracts.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardSericeImpl implements CardService {
    private final CardRepository cardRepository;

    @Autowired
    public CardSericeImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public Card createCard(User createdBy, Card card) {
        cardRepository.create(card);
        cardRepository.addCardToUser(createdBy.getId(), card.getId());
        return card;
    }

    @Override
    public void deleteCard(int card_id, User user) {
        Card card = cardRepository.getById(card_id);
        authorizeCardAccess(card.getId(), user);
        cardRepository.delete(card_id);
        cardRepository.removeCardFromUser(user.getId(), card_id);
    }

    @Override
    public Card updateCard(Card card, User user) {
        Card cardToUpdate = cardRepository.getById(card.getId());
        authorizeCardAccess(cardToUpdate.getId(), user);
        cardRepository.update(card);
        return card;
    }

    @Override
    public Card getCard(int card_id, User user) {
        Card card = cardRepository.getById(card_id);
        authorizeCardAccess(card.getId(), user);
        return cardRepository.getUserCard(user, card_id);
    }

    @Override
    public List<Card> getAllCards(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException("You are not allowed to see all cards");
        }
        return cardRepository.getAll();
    }

    private void authorizeCardAccess(int card_id, User user) {
        if (!cardRepository.getById(card_id).getCardHolder().equals(user) && !user.getRole().getName().equals("admin")){
            throw new UnauthorizedOperationException("You are not authorized for this operation");
        }
    }
}
