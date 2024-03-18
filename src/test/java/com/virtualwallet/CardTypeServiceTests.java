package com.virtualwallet;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.CardType;
import com.virtualwallet.models.Role;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardTypeRepository;
import com.virtualwallet.services.CardTypeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class CardTypeServiceTests {

    @Mock
    private CardTypeRepository cardTypeRepository;

    @InjectMocks
    private CardTypeServiceImpl cardTypeService;

    private User adminUser;
    private User regularUser;
    private CardType newCardType;

    @BeforeEach
    public void setUp() {
        Role userRole = new Role(1, "user");
        Role adminRole = new Role(2, "admin");

        regularUser = new User();
        regularUser.setId(2);
        regularUser.setUsername("regularUser");
        regularUser.setRole(userRole);

        adminUser = new User();
        adminUser.setId(1);
        adminUser.setUsername("adminUser");
        adminUser.setRole(adminRole);

        newCardType = new CardType(1,"Credit");
    }

    @Test
    public void whenCreateCardTypeAsAdmin_thenRoleIsCreated() {
        // When
        cardTypeService.createCardType(adminUser, newCardType);

        // Then
        Mockito.verify(cardTypeRepository, Mockito.times(1)).create(newCardType);
    }

    @Test
    public void whenCreateCardTypeAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> cardTypeService.createCardType(regularUser, newCardType));

        Mockito.verify(cardTypeRepository, Mockito.never()).create(Mockito.any(CardType.class));
    }

    @Test
    public void whenDeleteRoleAsAdmin_thenRoleIsDeleted() {
        // When
        cardTypeService.deleteCardType(3, adminUser);

        // Then
        Mockito.verify(cardTypeRepository, Mockito.times(1)).delete(3);
    }

    @Test
    public void whenDeleteRoleAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> cardTypeService.deleteCardType(3, regularUser));

        Mockito.verify(cardTypeRepository, never()).delete(3);
    }

    @Test
    public void whenUpdateRoleAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> cardTypeService.updateCardType(newCardType, regularUser));

        Mockito.verify(cardTypeRepository, never()).create(newCardType);
    }

    @Test
    public void whenUpdateRoleWithDuplicateName_thenThrowsDuplicateEntityException() {
        // Arrange
        Mockito.when(cardTypeRepository.getByStringField("name", newCardType.getType())).thenReturn(newCardType);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> cardTypeService.updateCardType(newCardType, adminUser));
    }

    @Test
    public void whenGetRole_thenRoleIsReturned() {
        // When
        cardTypeService.getCardType(3);

        // Then
        Mockito.verify(cardTypeRepository, Mockito.times(1)).getById(3);
    }

    @Test
    public void whenGetAllRolesAsAdmin_thenAllRolesAreReturned() {
        // When
        cardTypeService.getAllCardTypes();

        // Then
        Mockito.verify(cardTypeRepository, Mockito.times(1)).getAll();
    }

}
