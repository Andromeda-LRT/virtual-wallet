package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.ExpiredCardException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardRepository;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.utils.AESUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.virtualwallet.Helpers.createAnotherMockUser;
import static com.virtualwallet.Helpers.createMockCard;

@ExtendWith(MockitoExtension.class)
public class CardServiceTests {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void createCard_Should_CreateCard_When_CardDoesNotExist() throws Exception {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        card.setCardHolderId(user);
        String encryptedCardNumber = AESUtil.encrypt(card.getNumber());
        Mockito.when(cardRepository.getByStringField("number", encryptedCardNumber))
                .thenThrow(new EntityNotFoundException("Card", "number", card.getNumber()));

        //Act
        Card result = cardService.createCard(user, card);

        //Assert
        Mockito.verify(cardRepository, Mockito.times(1)).create(card);
        Assertions.assertEquals(card, result);
        Assertions.assertEquals(1, result.getId());
    }

    @Test
    void createCard_Should_UpdateCard_When_CardAlreadyExists() throws Exception {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        card.setArchived(true);
        user.getCards().add(card);
        card.setCardHolderId(user);
        String encryptedCardNumber = AESUtil.encrypt(card.getNumber());
        Mockito.when(cardRepository.getByStringField("number", encryptedCardNumber))
                .thenReturn(card);

        //Act
        Card result = cardService.createCard(user, card);

        //Assert
        Mockito.verify(cardRepository, Mockito.times(1)).update(card);
        Assertions.assertFalse(result.isArchived());
    }

    @Test
    void createCard_Should_ThrowDuplicateEntityException_When_CardAlreadyBelongsToUser() throws Exception  {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        user.getCards().add(card);
        card.setCardHolderId(user);

        Card anotherCard = createMockCard();
        anotherCard.setCardHolderId(user);
        String encryptedCardNumber = AESUtil.encrypt(card.getNumber());
        Mockito.when(cardRepository.getByStringField("number", encryptedCardNumber))
                .thenThrow(new EntityNotFoundException("Card", "number", card.getNumber()));
        card.setNumber(encryptedCardNumber);

        //Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> cardService.createCard(user, anotherCard));
    }

    @Test
    void createCard_Should_ThrowUnauthorizedOperationException_When_CardHolderIsDifferent() throws Exception{
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        User anotherMockUser = createAnotherMockUser();
        anotherMockUser.setFirstName("AnotherMock");
        anotherMockUser.setId(2);
        card.setCardHolderId(user);

        String encryptedCardNumber = AESUtil.encrypt(card.getNumber());
        Mockito.when(cardRepository.getByStringField("number", encryptedCardNumber))
                .thenThrow(new EntityNotFoundException("Card", "number", card.getNumber()));

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> cardService.createCard(anotherMockUser, card));
    }

    @Test
    void createCard_Should_Throw_ExpiredCardException_When_ExpirationDateIsInvalid() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        user.getCards().add(card);
        String str = "2023-01-01 23:59";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        card.setExpirationDate(LocalDateTime.parse(str, formatter));

        //Act & Assert
        Assertions.assertThrows(ExpiredCardException.class, () -> cardService.createCard(user, card));
    }

    @Test
    void deleteCard_Should_DeleteCard_When_ArgumentsValid() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        user.getCards().add(card);
        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);

        //Act
        cardService.deleteCard(card.getId(), user);

        //Assert
        Assertions.assertEquals(0, user.getCards().size());
        Assertions.assertTrue(card.isArchived());
        Mockito.verify(cardRepository, Mockito.times(1)).update(card);
    }

    @Test
    void deleteCard_Should_Throw_UnauthorizedOperationException_When_CardHolderIsDifferent() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        User anotherMockUser = createAnotherMockUser();
        anotherMockUser.setFirstName("AnotherMock");
        anotherMockUser.setId(2);
        card.setCardHolderId(user);

        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> cardService.deleteCard(card.getId(), anotherMockUser));
    }

    @Test
    void updateCard_Should_UpdateCard_When_Arguments_Valid() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        user.getCards().add(card);

        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);

        //Act
        Card result = cardService.updateCard(card, user);

        //Assert
        Assertions.assertEquals(result, card);
        Mockito.verify(cardRepository, Mockito.times(1)).update(card);
    }

    @Test
    void updateCard_Should_Throw_ExpiredCardException_When_ExpirationDateIsInvalid() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        user.getCards().add(card);
        String str = "2023-01-01 23:59";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        card.setExpirationDate(LocalDateTime.parse(str, formatter));

        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);

        //Act & Assert
        Assertions.assertThrows(ExpiredCardException.class, () -> cardService.updateCard(card, user));
    }

    @Test
    void getCard_Should_GetCard_When_ArgumentsValid() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        user.getCards().add(card);

        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);
        Mockito.when(userService.get(user.getId(), user)).thenReturn(user);
        Mockito.when(cardRepository.getUserCard(user, card.getId())).thenReturn(card);

        //Act
        Card result = cardService.getCard(card.getId(), user, user.getId());

        //Assert
        Assertions.assertEquals(result, card);
        Mockito.verify(cardRepository, Mockito.times(1)).getUserCard(user, card.getId());
    }

    @Test
    void getCard_Should_Throw_UnauthorizedOperationException_When_CardHolderIsDifferent() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        User anotherMockUser = createAnotherMockUser();
        anotherMockUser.setFirstName("AnotherMock");
        anotherMockUser.setId(2);
        card.setCardHolderId(user);

        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> cardService.getCard(card.getId(), anotherMockUser, anotherMockUser.getId()));
    }

    @Test
    void getCard_Should_Throw_EntityNotFoundException_When_CardIsDeleted() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        user.getCards().add(card);
        card.setArchived(true);

        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);
        Mockito.when(userService.get(user.getId(), user)).thenReturn(user);
        Mockito.when(cardRepository.getUserCard(user, card.getId())).thenReturn(card);

        //Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> cardService.getCard(card.getId(), user, user.getId()));
    }

    @Test
    void getAllUserCards_Should_GetUserCards_When_UserHasCards() throws Exception{
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        card.setNumber(AESUtil.encrypt(card.getNumber()));
        Card card2 = createMockCard();
        card2.setId(2);
        card2.setNumber(AESUtil.encrypt(card2.getNumber()));
        Card card3 = createMockCard();
        card3.setId(3);
        card3.setNumber(AESUtil.encrypt(card3.getNumber()));
        user.getCards().add(card);
        user.getCards().add(card2);
        user.getCards().add(card3);

        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);

        //Act
        List<Card> result = cardService.getAllUserCards(user);

        //Assert
        Assertions.assertEquals(3, result.size());
    }

    @Test
    void authorizeCardAccess_Should_Validate_When_ArgumentsValid() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();

        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);

        //Act
        cardService.authorizeCardAccess(card.getId(), user);

        //Assert
        Mockito.verify(cardRepository, Mockito.times(1)).getById(card.getId());
    }

    @Test
    void authorizeCardAccess_Should_ThrowUnauthorizedOperationException_When_CardHolderIsDifferent() {
        //Arrange
        User user = createAnotherMockUser();
        Card card = createMockCard();
        User anotherMockUser = createAnotherMockUser();
        anotherMockUser.setFirstName("AnotherMock");
        anotherMockUser.setId(2);

        Mockito.when(cardRepository.getById(card.getId())).thenReturn(card);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> cardService.authorizeCardAccess(card.getId(), anotherMockUser));
    }
}
