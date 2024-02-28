package com.virtualwallet.services;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.ExpiredCardException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardRepository;
import com.virtualwallet.services.contracts.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public Card createCard(User createdBy, Card card) {
        if (card.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredCardException("Card is expired");
        }
        Card cardToBeCreated;
        try {
            cardToBeCreated = cardRepository.getByStringField(card.getNumber());
            cardToBeCreated.setExpirationDate(card.getExpirationDate());
            cardToBeCreated.setArchived(false);
            cardRepository.update(cardToBeCreated);
        } catch (EntityNotFoundException e) {
            cardRepository.create(card);
            cardRepository.addCardToUser(createdBy.getId(), card.getId());
            return card;
        }

        cardRepository.addCardToUser(createdBy.getId(), cardToBeCreated.getId());
        return cardToBeCreated;
    }

    @Override
    public void deleteCard(int card_id, User user) {
        authorizeCardAccess(card_id, user);
        cardRepository.delete(card_id);
        cardRepository.removeCardFromUser(user.getId(), card_id);
    }

    @Override
    public Card updateCard(Card card, User user) {
        authorizeCardAccess(card.getId(), user);
        if (card.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredCardException("Card is expired");
        }
        cardRepository.update(card);
        return card;
    }

    @Override
    public Card getCard(int card_id, User user) {
        authorizeCardAccess(card_id, user);
        return cardRepository.getUserCard(user, card_id);
    }

    @Override
    public List<Card> getAllUserCards(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException("You are not allowed to see all cards");
        }
        return cardRepository.getAll();
    }

    private void authorizeCardAccess(int card_id, User user) {
        if (!cardRepository.getById(card_id).getCardHolder().equals(user) && !user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException("You are not authorized for this operation");
        }
    }
}
