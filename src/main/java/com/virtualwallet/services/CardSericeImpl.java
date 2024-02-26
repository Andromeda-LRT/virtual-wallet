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
        checkIfCardExists(card_id);
        authorizeCardAccess(card_id, user);
        cardRepository.delete(card_id);
        cardRepository.removeCardFromUser(user.getId(), card_id);
    }

    @Override
    public Card updateCard(Card card, User user) {
        checkIfCardExists(card.getId());
        authorizeCardAccess(card.getId(), user);
        cardRepository.update(card);
        return card;
    }

    @Override
    public Card getCard(int card_id, User user) {
        checkIfCardExists(card_id);
        authorizeCardAccess(card_id, user);
        return cardRepository.getUserCard(user, card_id);
    }

    @Override
    public List<Card> getAllCards(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException("You are not allowed to see all cards");
        }
        return cardRepository.getAll();
    }

    private void checkIfCardExists(int card_id) {
        if (!cardRepository.checkIfCardExists(card_id)) {
            throw new EntityNotFoundException("Card with id " + card_id + " does not exist");
        }
    }

    private void authorizeCardAccess(int cardId, User user) {
        if (!cardRepository.isCardBelongToUser(user.getId(), cardId) && !user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException("You are not authorized for this operation");
        }
    }
}
