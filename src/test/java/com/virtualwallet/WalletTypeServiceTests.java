package com.virtualwallet;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Role;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletType;
import com.virtualwallet.repositories.contracts.WalletTypeRepository;
import com.virtualwallet.services.WalletTypeServiceImpl;
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
public class WalletTypeServiceTests {
    @Mock
    private WalletTypeRepository walletTypeRepository;

    @InjectMocks
    private WalletTypeServiceImpl walletTypeService;

    private User adminUser;
    private User regularUser;

    private WalletType newWalletType;

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

        newWalletType = new WalletType(1,"Credit");
    }

    @Test
    public void whenCreateWalletTypeAsAdmin_thenWalletTypeIsCreated() {
        // When
        walletTypeService.createWalletType(adminUser, newWalletType);

        // Then
        Mockito.verify(walletTypeRepository, Mockito.times(1)).create(newWalletType);
    }

    @Test
    public void whenCreateWalletTypeAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> walletTypeService.createWalletType(regularUser, newWalletType));

        Mockito.verify(walletTypeRepository, Mockito.never()).create(Mockito.any(WalletType.class));
    }

    @Test
    public void whenDeleteWalletTypeAsAdmin_thenWalletTypeIsDeleted() {
        // When
        walletTypeService.deleteWalletType(3, adminUser);

        // Then
        Mockito.verify(walletTypeRepository, Mockito.times(1)).delete(3);
    }

    @Test
    public void whenDeleteWalletTypeAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> walletTypeService.deleteWalletType(3, regularUser));

        Mockito.verify(walletTypeRepository, never()).delete(3);
    }

    @Test
    public void whenUpdateWalletTypeAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> walletTypeService.updateWalletType(newWalletType, regularUser));

        Mockito.verify(walletTypeRepository, never()).create(newWalletType);
    }

    @Test
    public void whenUpdateWalletTypeWithDuplicateName_thenThrowsDuplicateEntityException() {
        // Arrange
        Mockito.when(walletTypeRepository.getByStringField("name", newWalletType.getType())).thenReturn(newWalletType);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> walletTypeService.updateWalletType(newWalletType, adminUser));
    }

    @Test
    public void whenGetWalletType_thenWalletTypeIsReturned() {
        // When
        walletTypeService.getWalletType(3);

        // Then
        Mockito.verify(walletTypeRepository, Mockito.times(1)).getById(3);
    }

    @Test
    public void whenGetAllWalletTypesAsAdmin_thenAllWalletTypesAreReturned() {
        // When
        walletTypeService.getAllWalletTypes();

        // Then
        Mockito.verify(walletTypeRepository, Mockito.times(1)).getAll();
    }
}
