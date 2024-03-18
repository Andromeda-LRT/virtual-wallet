package com.virtualwallet;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.CheckNumber;
import com.virtualwallet.models.Role;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CheckNumberRepository;
import com.virtualwallet.services.CheckNumberServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CheckNumberServiceTests {
    @Mock
    private CheckNumberRepository checkNumberRepository;
    @InjectMocks
    private CheckNumberServiceImpl checkNumberService;

    private User adminUser;
    private User regularUser;

    private CheckNumber newCheckNumber;

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

        newCheckNumber = new CheckNumber("123");
    }

    @Test
    public void createCheckNumber_Should_CallRepository_When_CheckNumberDoesNotExist() {
        CheckNumber checkNumber = new CheckNumber("444");

        Mockito.when(checkNumberRepository.getByNumber(checkNumber.getCvv()))
                .thenThrow(EntityNotFoundException.class);

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> checkNumberService.createCheckNumber(checkNumber.getCvv()));

    }

    @Test
    public void createCheckNumber_Should_ReturnCheckNumber_IfCheckNumberExists() {
        // Arrange
        String cvv = "123";
        CheckNumber checkNumber = new CheckNumber(cvv);
        Mockito.when(checkNumberRepository.getByNumber(cvv)).thenReturn(checkNumber);

        // Act
        CheckNumber result = checkNumberService.createCheckNumber(cvv);

        // Assert
        Assertions.assertEquals(checkNumber, result);
    }


    @Test
    public void deleteCheckNumber_Should_ThrowUnauthorizedOperationException_IfUserNotAdmin() {
        // Arrange
        int checkNumberId = 1;

        // Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> checkNumberService.deleteCheckNumber(checkNumberId, regularUser));
        Mockito.verify(checkNumberRepository, Mockito.never()).delete(checkNumberId);
    }

    @Test
    public void deleteCheckNumber_Should_DeleteCheckNumber_IfUserIsAdmin() {
        // Arrange
        int checkNumberId = 1;

        // Act
        checkNumberService.deleteCheckNumber(checkNumberId, adminUser);

        // Assert
        Mockito.verify(checkNumberRepository, Mockito.times(1)).delete(checkNumberId);
    }

    @Test
    public void updateCheckNumber_Should_ThrowUnauthorizedOperationException_IfUserNotAdmin() {
        // Arrange
        CheckNumber checkNumber = new CheckNumber("123");

        // Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> checkNumberService.updateCheckNumber(checkNumber, regularUser));
        Mockito.verify(checkNumberRepository, Mockito.never()).update(checkNumber);
    }

    @Test
    public void updateCheckNumber_Should_ThrowDuplicateEntityException_IfCheckNumberExists() {
        // Arrange
        String cvv = "123";
        Mockito.when(checkNumberRepository.getByNumber(cvv)).thenReturn(new CheckNumber(cvv));

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> checkNumberService.updateCheckNumber(newCheckNumber, adminUser));
    }

    @Test
    public void updateCheckNumber_Should_Thrown_When_CheckNumberExists() {
        // Arrange
        String cvv = "123";
        CheckNumber checkNumber = new CheckNumber(cvv);

        Mockito.when(checkNumberRepository.getByNumber(checkNumber.getCvv()))
                .thenReturn(checkNumber);

        // Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> checkNumberService.updateCheckNumber(checkNumber, adminUser));
        Mockito.verify(checkNumberRepository, Mockito.never()).update(checkNumber);
    }

    @Test
    public void getCheckNumberById_Should_ReturnCheckNumber_When_CheckNumberExists() {
        // When
        checkNumberService.getCheckNumberById(1);

        // Then
        Mockito.verify(checkNumberRepository, Mockito.times(1)).getById(1);
    }

    @Test
    public void getAllCheckNumbers_Should_ReturnCheckNumbers() {
        // When
        checkNumberService.getAllCheckNumbers();

        // Then
        Mockito.verify(checkNumberRepository, Mockito.times(1)).getAll();
    }

    @Test
    public void getCheckNumberByNumber_Should_ReturnCheckNumbers() {
        // When
        String cvv = "123";
        checkNumberService.getCheckNumberByNumber(cvv);

        // Then
        Mockito.verify(checkNumberRepository, Mockito.times(1)).getByNumber(cvv);
    }
}