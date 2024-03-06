package com.virtualwallet.services;

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

//  TODO Add CvvNumberService - LYUBIMA - DONE
    private final CheckNumberService checkNumberService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, UserService userService, CheckNumberService checkNumberService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.checkNumberService = checkNumberService;
    }

    @Override
    public Card createCard(User createdBy, Card card, String cardHolder) {
        verifyCardExpirationDate(card);
        checkCardHolder(createdBy, cardHolder);
        Card cardToBeCreated;
        try {
            // Encrypt the card number before checking/using it in the repository
            String encryptedCardNumber = encryptCardNumber(card.getNumber());
            cardToBeCreated = cardRepository.getByStringField("number", encryptedCardNumber);
            cardToBeCreated.setExpirationDate(card.getExpirationDate());

            // Ensure the card number is stored encrypted in the repository
            cardToBeCreated.setNumber(encryptedCardNumber);
            cardRepository.update(cardToBeCreated);
        } catch (EntityNotFoundException e) {
            // Encrypt the card number before saving the new card

            //TODO Check if cvv number is already created - if not create it - LYUBIMA
//            checkNumberService.createCheckNumber(card.getCheckNumber().getCvv());

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
        cardRepository.getByStringField("id", String.valueOf(card_id));

        // Todo implement a method for removing the card
        //  from the user list of cards
        //  user.removeCard(card_id);
        // then update user
        //  userService.update(user);
        // then make soft delete
        cardRepository.removeCardFromUser(user.getId(), card_id);

        // TODO Change delete method logic - LYUBIMA
        //  this method should go and update
        //  card field isArchived to true
        cardRepository.delete(card_id);
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

    @Override
    public void verifyCardExistence(int cardId) {
        cardRepository.getByStringField("id", String.valueOf(cardId));
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

    private void checkCardHolder(User loggedUser, String cardHolderFullName) {
        StringBuilder loggedUserFullName = new StringBuilder();
        loggedUserFullName.append(loggedUser.getFirstName()).append(" ").append(loggedUser.getLastName());

        if (!loggedUserFullName.toString().equalsIgnoreCase(cardHolderFullName)
                && !loggedUser.getRole().getName().equals("admin")) {
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
