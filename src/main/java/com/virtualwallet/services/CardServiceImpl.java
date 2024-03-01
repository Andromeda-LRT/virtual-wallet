package com.virtualwallet.services;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.ExpiredCardException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardRepository;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.utils.AESUtil;
import com.virtualwallet.utils.UtilHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    private UtilHelpers utilHelpers;
    private final CardRepository cardRepository;
    private final UserService userService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    @Override
    public Card createCard(User createdBy, Card card) {
        verifyCardExpirationDate(card);
        verifyCardHolderAccess(createdBy, card);
        Card cardToBeCreated;
        try {
            // Encrypt the card number before checking/using it in the repository
            String encryptedCardNumber = encryptCardNumber(card.getNumber());
            cardToBeCreated = cardRepository.getByStringField(encryptedCardNumber);
            cardToBeCreated.setExpirationDate(card.getExpirationDate());
            cardToBeCreated.setArchived(false);
            // Ensure the card number is stored encrypted in the repository
            cardToBeCreated.setNumber(encryptedCardNumber);   //TODO: This can be commented out - LYUBIMA
            cardRepository.update(cardToBeCreated);
        } catch (EntityNotFoundException e) {
            // Encrypt the card number before saving the new card
            card.setNumber(encryptCardNumber(card.getNumber()));
            cardRepository.create(card);
            cardRepository.addCardToUser(createdBy.getId(), card.getId());
            card.setNumber(decryptCardNumber(card.getNumber()));
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
        verifyCardExpirationDate(card);
        cardRepository.update(card);
        card.setNumber(decryptCardNumber(card.getNumber()));
        return card;
    }

    @Override
    public Card getCard(int card_id, User user) {
        authorizeCardAccess(card_id, user);
        return cardRepository.getUserCard(user, card_id);
    }

    @Override
    public List<Card> getAllUserCards(User user) {
        List<Card> cardsWithDecryptedNumbers = new ArrayList<>();
        for (Card card : cardRepository.getAllUserCards(user.getId())) {
            card.setNumber(decryptCardNumber(card.getNumber()));
            cardsWithDecryptedNumbers.add(card);
        }
        return cardsWithDecryptedNumbers;
    }

    private void authorizeCardAccess(int card_id, User user) {
        if (!cardRepository.getById(card_id).getCardHolder().equals(user) && !user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException("You are not authorized for this operation");
        }
    }

    private String encryptCardNumber(String cardNumber) {
        try {
            return AESUtil.encrypt(cardNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decryptCardNumber(String encryptedCardNumber) {
        try {
            return AESUtil.decrypt(encryptedCardNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void verifyCardExpirationDate(Card card){
        if (card.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredCardException("Card is expired");
        }
    }

    private void verifyCardHolderAccess(User user, Card card){
        if(!card.getCardHolder().equals(createdBy)){
            throw new UnauthorizedOperationException("You are not authorized for this operation");
        }
    }
}