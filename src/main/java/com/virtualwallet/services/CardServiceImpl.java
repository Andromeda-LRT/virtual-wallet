package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.ExpiredCardException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardRepository;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.CheckNumberService;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.utils.AESUtil;
import com.virtualwallet.utils.UtilHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

@Service
public class CardServiceImpl implements CardService {
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
    Card cardToBeCreated;
    try {
        // Encrypt the card number before checking/using it in the repository
        String encryptedCardNumber = encryptCardNumber(card.getNumber());
        cardToBeCreated = cardRepository.getByStringField("number", encryptedCardNumber);
        checkCardHolder(createdBy, cardToBeCreated);

        cardToBeCreated.setExpirationDate(card.getExpirationDate());
        // Ensure the card number is stored encrypted in the repository
        cardRepository.update(cardToBeCreated);
    } catch (EntityNotFoundException e) {
        // Encrypt the card number before saving the new card
        checkCardHolder(createdBy, card);
        card.setNumber(encryptCardNumber(card.getNumber()));
        card.setCardHolderId(createdBy);
        cardRepository.create(card);
        addCardToUser(createdBy, card);
        card.setNumber(decryptCardNumber(card.getNumber()));
        return card;
    }

    addCardToUser(createdBy, cardToBeCreated);
    cardToBeCreated.setNumber(decryptCardNumber(cardToBeCreated.getNumber()));
    return cardToBeCreated;
}

    private void addCardToUser(User user, Card card) {
        if (user.getCards().contains(card)) {
            throw new DuplicateEntityException("Card", "number",
                    String.valueOf(decryptCardNumber(card.getNumber())));
        }
        user.getCards().add(card);
        userService.update(user, user);
    }

    @Override
    public void deleteCard(int card_id, User user) {
        authorizeCardAccess(card_id, user);
        Card card = cardRepository.getById(card_id);
        user.getCards().remove(card);
        card.setArchived(true);
        userService.update(user, user);
        cardRepository.update(card);
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
    public Card getCard(int card_id, User loggedUser, int userId) {
        authorizeCardAccess(card_id, loggedUser);
        User user = userService.get(userId, loggedUser);
        return cardRepository.getUserCard(user, card_id);
    }

    @Override
    public List<Card> getAllUserCards(User user) {
        userService.getByUsername(user.getUsername());

        List<Card> cardsWithDecryptedNumbers = new ArrayList<>();
        for (Card card : user.getCards()) {
            card.setNumber(decryptCardNumber(card.getNumber()));
            cardsWithDecryptedNumbers.add(card);
        }
        return cardsWithDecryptedNumbers;
    }

    @Override
    public void authorizeCardAccess(int card_id, User user) {
        StringBuilder cardHolderFullName = new StringBuilder();
        cardHolderFullName.append(user.getFirstName()).append(" ").append(user.getLastName());

        if (!cardRepository.getById(card_id).getCardHolder().equals(cardHolderFullName.toString())
                && !user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }

    private void checkCardHolder(User loggedUser, Card card) {
        StringBuilder loggedUserFullName = new StringBuilder();
        loggedUserFullName.append(loggedUser.getFirstName()).append(" ").append(loggedUser.getLastName());

        if (!(loggedUserFullName.toString().equalsIgnoreCase(card.getCardHolder()))
         || !(loggedUser.getId() == card.getCardHolderId().getId())) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
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

    private void verifyCardExpirationDate(Card card) {
        if (card.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredCardException(EXPIRED_CARD_ERROR_MESSAGE);
        }
    }
}
