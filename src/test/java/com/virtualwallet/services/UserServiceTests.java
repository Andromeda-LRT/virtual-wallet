package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.exceptions.UnusedWalletBalanceException;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.models.Role;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.mvc_input_model_dto.UpdateUserPasswordDto;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.utils.PasswordEncoderUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.virtualwallet.Helpers.*;
import static com.virtualwallet.model_helpers.ModelConstantHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private List<User> exampleUsers;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role(1, "admin");
        User user1 = new User();
        User user2 = new User();
        exampleUsers = Arrays.asList(user1, user2);

        user = new User(1, "testUser", "Pass1234!", "Ivan", "Ivanov",
                "testUser@example.com", role, false, false,
                "1234567890", null, null, null);
    }

    @Test
    void getAll_ReturnsAllUsers() {
        // Arrange
        when(userRepository.getAll()).thenReturn(exampleUsers);

        // Act
        List<User> result = userService.getAll();

        // Assert
        assertEquals(exampleUsers, result);
        verify(userRepository).getAll();
    }

    @Test
    void getAllWithFilter_ReturnsFilteredUsers() {
        // Arrange
        UserModelFilterOptions userFilter = new UserModelFilterOptions();
        when(userRepository.getAllWithFilter(userFilter)).thenReturn(exampleUsers);

        // Act
        List<User> result = userService.getAllWithFilter(new User(), userFilter);

        // Assert
        assertEquals(exampleUsers, result);
        verify(userRepository).getAllWithFilter(userFilter);
    }

    @Test
    void getRecipient_ReturnsFilteredRecipients() {
        // Arrange
        UserModelFilterOptions userFilter = new UserModelFilterOptions();
        when(userRepository.getAllWithFilter(userFilter)).thenReturn(exampleUsers);

        // Act
        List<User> result = userService.getRecipient(userFilter);

        // Assert
        assertEquals(exampleUsers, result);
        verify(userRepository).getAllWithFilter(userFilter);
    }


    @Test
    void get_ReturnsUserById_AfterVerifyingAccess() {
        // Arrange
        int userId = 1;
        Role role = new Role(1, "admin");
        User accessingUser = new User();
        accessingUser.setRole(role);
        User expectedUser = new User();
        when(userRepository.getById(userId)).thenReturn(expectedUser);

        // Act
        User result = userService.get(userId, accessingUser);

        // Assert
        assertEquals(expectedUser, result);

    }

    @Test
    void getByUsername_ReturnsUserWithSpecifiedUsername() {
        // Arrange
        String username = "testUser";
        User expectedUser = new User();
        when(userRepository.getByStringField(USER_USERNAME, username)).thenReturn(expectedUser);

        // Act
        User result = userService.getByUsername(username);

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    void getByEmail_ReturnsUserWithEmail() {
        // Arrange
        String email = "user@example.com";
        User expectedUser = new User();
        when(userRepository.getByStringField(USER_EMAIL, email)).thenReturn(expectedUser);

        // Act
        User result = userService.getByEmail(email);

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    void getByPhone_ReturnsUserWithPhoneNumber() {
        // Arrange
        String phone = "1234567890";
        User expectedUser = new User();
        when(userRepository.getByStringField(USER_PHONE_NUMBER, phone)).thenReturn(expectedUser);

        // Act
        User result = userService.getByPhone(phone);

        // Assert
        assertEquals(expectedUser, result);
    }


    @Test
    void create_SuccessfullyCreatesUser_WhenNoDuplicatesExist() {
        when(userRepository.getByStringField(anyString(), anyString()))
                .thenThrow(EntityNotFoundException.class);

        // Act
        userService.create(user);

        // Assert
        verify(userRepository).create(any(User.class));
    }


    @Test
    void create_ThrowsDuplicateEntityException_WhenDuplicateUsernameExists() {
        when(userRepository.getByStringField("username", user.getUsername())).thenReturn(new User());

        // Act & Assert
        assertThrows(DuplicateEntityException.class, () -> userService.create(user));
    }

    @Test
    void create_ThrowsDuplicateEntityException_WhenDuplicateEmailExists() {
        when(userRepository.getByStringField("username", user.getUsername())).thenThrow(EntityNotFoundException.class);
        when(userRepository.getByStringField("email", user.getEmail())).thenReturn(new User());

        // Act & Assert
        assertThrows(DuplicateEntityException.class, () -> userService.create(user));
    }

    @Test
    void create_ThrowsDuplicateEntityException_WhenDuplicatePhoneNumberExists() {
        when(userRepository.getByStringField("username", user.getUsername())).thenThrow(EntityNotFoundException.class);
        when(userRepository.getByStringField("email", user.getEmail())).thenThrow(EntityNotFoundException.class);
        when(userRepository.getByStringField("phoneNumber", user.getPhoneNumber())).thenReturn(new User());

        // Act & Assert
        assertThrows(DuplicateEntityException.class, () -> userService.create(user));
    }

    @Test
    void update_SuccessfullyUpdatesUser() {
        doNothing().when(userRepository).update(any(User.class));
        when(userRepository.getByStringField(anyString(), anyString()))
                .thenThrow(EntityNotFoundException.class);

        // Act
        User result = userService.update(user, user);

        // Assert
        assertEquals(user, result);
        verify(userRepository).update(user);
    }

    @Test
    void update_ThrowsDuplicateEntityException_WhenDuplicateUsernameExists() {

        User existingUserWithSameUsername = new User();
        existingUserWithSameUsername.setId(2);
        when(userRepository.getByStringField("username", "testUser")).thenReturn(existingUserWithSameUsername);

        // Act & Assert
        assertThrows(DuplicateEntityException.class, () -> userService.update(user, user));
    }

    @Test
    void delete_SuccessfullyArchivesUser_WhenNoUnusedWalletBalance() {
        Wallet wallet = new Wallet(1, "111111", 0, false, "walletName", 1, 1);
        Set<Wallet> walletSet = new HashSet<>();
        walletSet.add(wallet);
        user.setWallets(walletSet);
        // Arrange
        when(userRepository.getById(user.getId())).thenReturn(user);

        // Act
        userService.delete(user.getId(), user);

        // Assert
        assertTrue(user.isArchived());
        verify(userRepository).update(user);
    }

    @Test
    void delete_ThrowsUnusedWalletBalanceException_WhenPositiveBalanceExists() {
        // Arrange
        Wallet wallet = new Wallet(1, "111111", 100, false, "walletName", 1, 1);
        Set<Wallet> walletSet = new HashSet<>();
        walletSet.add(wallet);
        user.setWallets(walletSet);

        when(userRepository.getById(user.getId())).thenReturn(user);

        // Act & Assert
        assertThrows(UnusedWalletBalanceException.class, () -> userService.delete(user.getId(), user));
    }

    @Test
    void blockUser_Should_CallRepository_When_ArgumentsValid() {
        //Arrange
        User userAdmin = createMockAdminUser();
        User normalUser = createAnotherMockUser();

        Mockito.when(userRepository.getById(userAdmin.getId())).thenReturn(userAdmin);

        //Act
        userService.blockUser(normalUser.getId(), userAdmin);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).blockUser(normalUser.getId());
    }

    @Test
    void blockUser_Should_Throw_UnauthorizedOperationException_When_UserIsNotAdmin() {
        //Arrange
        User normalUser = createAnotherMockUser();
        User anotherUser = createAnotherMockUser();
        anotherUser.setId(4);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.blockUser(anotherUser.getId(), normalUser));
    }


    @Test
    void unblockUser_Should_CallRepository_When_ArgumentsValid() {
        //Arrange
        User userAdmin = createMockAdminUser();
        User normalUser = createAnotherMockUser();

        Mockito.when(userRepository.getById(userAdmin.getId())).thenReturn(userAdmin);

        //Act
        userService.unblockUser(normalUser.getId(), userAdmin);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).unblockUser(normalUser.getId());
    }

    @Test
    void unblockUser_Should_Throw_UnauthorizedOperationException_When_UserIsNotAdmin() {
        //Arrange
        User normalUser = createAnotherMockUser();
        User anotherUser = createAnotherMockUser();
        anotherUser.setId(4);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.unblockUser(anotherUser.getId(), normalUser));
    }

    @Test
    void giveUserAdminRights_Should_CallRepository_When_ArgumentsValid() {
        //Arrange
        User userAdmin = createMockAdminUser();
        User normalUser = createAnotherMockUser();

        //Act
        userService.giveUserAdminRights(normalUser, userAdmin);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1)).giveUserAdminRights(normalUser);
    }

    @Test
    void giveUserAdminRights_Should_CallRepository_When_UserIsBlocked() {
        //Arrange
        User userAdmin = createMockAdminUser();
        User normalUser = createAnotherMockUser();
        normalUser.setBlocked(true);

        //Act
        userService.giveUserAdminRights(normalUser, userAdmin);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1)).unblockUser(normalUser.getId());

    }

    @Test
    void giveUserAdminRights_Should_ThrowUnauthorizedOperationException_When_UserIsNotAdmin() {
        //Arrange
        User userAdmin = createMockAdminUser();
        User normalUser = createAnotherMockUser();

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.giveUserAdminRights(userAdmin, normalUser));
    }

    @Test
    void removeUserAdminRights_Should_CallRepository_When_ArgumentsValid() {
        //Arrange
        User userAdmin = createMockAdminUser();
        User normalUser = createAnotherMockUser();

        //Act
        userService.removeUserAdminRights(normalUser, userAdmin);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1)).removeUserAdminRights(normalUser);
    }

    @Test
    void removeUserAdminRights_Should_ThrowUnauthorizedOperationException_When_UserIsNotAdmin() {
        //Arrange
        User userAdmin = createMockAdminUser();
        User normalUser = createAnotherMockUser();

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.removeUserAdminRights(userAdmin, normalUser));
    }

    @Test
    void verifyUserAccess_Should_ThrowUnauthorizedOperationException_When_UserIsNotAdmin() {
        //Arrange
        User userAdmin = createMockAdminUser();
        User normalUser = createAnotherMockUser();

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.verifyUserAccess(normalUser, userAdmin.getId()));
    }

    @Test
    void verifyUserAccess_Should_ThrowUnauthorizedOperationException_When_UserIdIsDifferentFromLoggedUser() {
        //Arrange
        User userAdmin = createMockAdminUser();
        userAdmin.setRole(createAnotherMockRole());
        User normalUser = createAnotherMockUser();

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.verifyUserAccess(normalUser, userAdmin.getId()));
    }

    @Test
    void verifyUserExistence_Should_ReturnUserIfUserExists() {
        //Arrange
        User userAdmin = createMockAdminUser();

        Mockito.when(userRepository.getById(userAdmin.getId())).thenReturn(userAdmin);

        //Act
        User resultUser = userService.verifyUserExistence(userAdmin.getId());

        //Assert
        Assertions.assertEquals(resultUser, userAdmin);
    }

    @Test
    void confirmIfPasswordsMatch_Should_ReturnTrue_When_BothPasswordsMatch() {
        //Arrange
        User normalUser = createAnotherMockUser();
        UpdateUserPasswordDto dto = createMockPasswordDto();
        dto.setCurrentPassword(normalUser.getPassword());
        normalUser.setPassword(PasswordEncoderUtil.encodePassword(normalUser.getPassword()));

        Mockito.when(userRepository.getById(normalUser.getId())).thenReturn(normalUser);

        //Act
        boolean result = userService.confirmIfPasswordsMatch(normalUser.getId(), dto);

        //Assert
        Assertions.assertTrue(result);
    }

    @Test
    void confirmIfPasswordsMatch_Should_ReturnFalse_When_PasswordsMismatch() {
        //Arrange
        User normalUser = createAnotherMockUser();
        UpdateUserPasswordDto dto = createMockPasswordDto();
        normalUser.setPassword(PasswordEncoderUtil.encodePassword(normalUser.getPassword()));

        Mockito.when(userRepository.getById(normalUser.getId())).thenReturn(normalUser);

        //Act
        boolean result = userService.confirmIfPasswordsMatch(normalUser.getId(), dto);

        //Assert
        Assertions.assertFalse(result);
    }

    @Test
    void isUserBlocked_Should_ThrowUnauthorizedOperationException_When_UserIsBlocked() {
        //Arrange
        User normalUser = createAnotherMockUser();
        normalUser.setBlocked(true);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.isUserBlocked(normalUser));
    }

}
