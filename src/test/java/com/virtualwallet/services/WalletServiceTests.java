package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.LimitReachedException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.exceptions.UnusedWalletBalanceException;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.*;
import com.virtualwallet.repositories.contracts.WalletRepository;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.CardTransactionService;
import com.virtualwallet.services.contracts.WalletTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTests {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private CardService cardService;

    @Mock
    private CardTransactionService cardTransactionService;

    @Mock
    private WalletTransactionService walletTransactionService;
    UserModelFilterOptions userFilter;

    private User user;
    private List<Wallet> testWallets;

    private List<User> expectedUsers;

    private Wallet wallet;

    @BeforeEach
    void setUp() {

        wallet = new Wallet(1, "111111", 200, false, "walletName", 1, 1);
        Set<Wallet> walletSet = new HashSet<>();
        Set<Card> cardSet = new HashSet<>();
        Role role = new Role();
        user = new User(1, "ivan", "Pass1234!", "Ivan", "Ivanov",
                "email@email.com", role, false, false, null, "0976666425",
                walletSet, cardSet);
        testWallets = Arrays.asList(new Wallet(), new Wallet());
        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                null, null, null, null, null);
    }

    @Test
    void getAllWallets_ShouldReturnWallets_ForGivenUser() {
        // Arrange
        when(walletRepository.getAllWallets(user)).thenReturn(testWallets);

        // Act
        List<Wallet> result = walletService.getAllWallets(user);

        // Assert
        verify(walletRepository).getAllWallets(user);
        assertEquals(testWallets.size(), result.size());
    }

    @Test
    void getAllPersonalWallets_ShouldReturnPersonalWallets_ForGivenUser() {
        // Arrange
        User user = new User();
        user.setId(1);

        Wallet personalWallet = new Wallet();
        personalWallet.setWalletTypeId(1);

        Wallet otherWallet = new Wallet();
        otherWallet.setWalletTypeId(2);


        Set<Wallet> wallets = new HashSet<>();
        wallets.add(personalWallet);
        wallets.add(otherWallet);

        when(userService.get(anyInt(), eq(user))).thenReturn(user);
        user.setWallets(wallets);

        // Act
        List<Wallet> result = walletService.getAllPersonalWallets(user);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(wallet -> wallet.getWalletTypeId() == 1));
    }

    @Test
    void getAllJoinWallets_ShouldReturnJoinWallets_ForGivenUser() {
        // Arrange
        User user = new User();
        user.setId(1);

        Wallet joinWallet = new Wallet();
        joinWallet.setWalletTypeId(WALLET_TYPE_ID_2);

        Wallet otherWallet = new Wallet();
        otherWallet.setWalletTypeId(WALLET_TYPE_ID_1);


        Set<Wallet> wallets = new HashSet<>();
        wallets.add(joinWallet);
        wallets.add(otherWallet);

        when(userService.get(anyInt(), eq(user))).thenReturn(user);
        user.setWallets(wallets);

        // Act
        List<Wallet> result = walletService.getAllJoinWallets(user);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(wallet -> wallet.getWalletTypeId() == WALLET_TYPE_ID_2));
    }

    @Test
    void getRecipient_ShouldReturnUsersBasedOnFilter() {
        // Arrange
        when(userService.getRecipient(userFilter)).thenReturn(expectedUsers);

        // Act
        List<User> result = walletService.getRecipient(userFilter);

        // Assert
        verify(userService).getRecipient(userFilter);
        assertEquals(expectedUsers, result);
    }

    @Test
    void createWallet_ShouldCreateAndReturnWallet() {
        // Arrange

        Mockito.doNothing().when(walletRepository).create(any(Wallet.class));

        Mockito.when(userService.update(any(User.class), any(User.class))).thenReturn(user);
        // Act
        Wallet createdWallet = walletService.createWallet(user, wallet);

        // Assert
        Mockito.verify(walletRepository, Mockito.times(1)).create(wallet);
        Mockito.verify(userService, Mockito.times(1)).update(user, user);
        assertEquals(user.getId(), createdWallet.getCreatedBy());
        assertTrue(user.getWallets().contains(wallet));
    }


    @Test
    void updateWallet_ShouldUpdateAndReturnWallet_WhenWalletExistsAndUserIsOwner() {
        WalletServiceImpl spyWalletService = Mockito.spy(walletService);
        User user = new User();
        user.setId(1);
        Wallet wallet = new Wallet();
        wallet.setWalletId(1);


        doNothing().when(walletRepository).update(wallet);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        doReturn(true).when(spyWalletService).verifyIfUserIsWalletOwner(wallet, user);
        Wallet updatedWallet = spyWalletService.updateWallet(user, wallet);


        Mockito.verify(walletRepository).update(wallet);
        assertEquals(wallet, updatedWallet, "The updated wallet should be returned.");
    }

    @Test
    void delete_ThrowsUnusedWalletBalanceException_WhenWalletHasBalance() {
        WalletServiceImpl spyWalletService = Mockito.spy(walletService);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        doReturn(true).when(spyWalletService).verifyIfUserIsWalletOwner(wallet, user);

        Exception exception = assertThrows(UnusedWalletBalanceException.class, () -> {
            spyWalletService.delete(user, wallet.getWalletId());
        });

        assertEquals(String.valueOf(String.format("Wallet has %.1f available balance", wallet.getBalance())), exception.getMessage());
    }

    @Test
    void delete_ThrowsUnauthorizedOperationException_WhenUserIsNotCreator() {
        WalletServiceImpl spyWalletService = Mockito.spy(walletService);
        user.setId(1);
        wallet.setCreatedBy(2);
        wallet.setBalance(0);
        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        doReturn(true).when(spyWalletService).verifyIfUserIsWalletOwner(wallet, user);

        Exception exception = assertThrows(UnauthorizedOperationException.class, () -> {
            spyWalletService.delete(user, wallet.getWalletId());
        });

        assertEquals(PERMISSIONS_ERROR_GENERAL, exception.getMessage());
    }

    @Test
    void delete_SuccessfullyDeletesWallet_WhenUserIsCreatorAndBalanceIsZero() {
        WalletServiceImpl spyWalletService = Mockito.spy(walletService);

        wallet.setBalance(0);
        Set<Wallet> userWallets = new HashSet<>();
        userWallets.add(wallet);
        user.setWallets(userWallets);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        doReturn(true).when(spyWalletService).verifyIfUserIsWalletOwner(wallet, user);

        Mockito.doNothing().when(walletRepository).update(any(Wallet.class));
        Mockito.doReturn(user).when(userService).update(any(User.class), any(User.class));

        // Act
        spyWalletService.delete(user, wallet.getWalletId());

        // Assert
        assertTrue(wallet.isArchived());
        assertFalse(user.getWallets().contains(wallet));
        Mockito.verify(walletRepository).update(wallet);
        Mockito.verify(userService).update(user, user);
    }


    @Test
    void getUserWalletTransactions_ShouldReturnTransactions_WhenWalletBelongsToUser() {
        WalletTransactionModelFilterOptions transactionFilter = new WalletTransactionModelFilterOptions(null,
                null, null, null, null, null, null);
        int walletId = 1;
        List<WalletToWalletTransaction> expectedTransactions = List.of(new WalletToWalletTransaction());


        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());

        when(walletTransactionService.getUserWalletTransactions(user, transactionFilter, walletId))
                .thenReturn(expectedTransactions);

        // Act
        List<WalletToWalletTransaction> transactions = walletService.getUserWalletTransactions(transactionFilter, user, walletId);

        // Assert
        assertEquals(expectedTransactions, transactions);
        verify(walletTransactionService).getUserWalletTransactions(user, transactionFilter, walletId);
    }

    @Test
    void walletToWalletTransaction_ThrowsException_WhenUserIsBlocked() {
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();

        doThrow(new RuntimeException("User is blocked")).when(userService).isUserBlocked(user);

        Exception exception = assertThrows(RuntimeException.class, () ->
                walletService.walletToWalletTransaction(user, 1, transaction)
        );

        assertEquals("User is blocked", exception.getMessage());
    }


    @Test
    void walletToWalletTransaction_ThrowsException_WhenSenderWalletBalanceIsInsufficient() {
        int senderWalletId = 1;
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();
        transaction.setRecipientWalletId(2);
        transaction.setAmount(500);

        Wallet senderWallet = new Wallet();
        senderWallet.setBalance(100);

        doNothing().when(userService).isUserBlocked(user);
        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());

        Exception exception = assertThrows(RuntimeException.class, () ->
                walletService.walletToWalletTransaction(user, senderWalletId, transaction)
        );

        assertTrue(exception.getMessage().contains("more funds to complete the transaction"));
    }

    @Test
    void walletToWalletTransaction_SuccessfulTransaction() {
        user.setId(1);
        Wallet senderWallet = new Wallet();
        senderWallet.setBalance(1000);
        Wallet recipientWallet = new Wallet();
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();
        transaction.setRecipientWalletId(2);
        transaction.setAmount(100);

        doNothing().when(userService).isUserBlocked(user);
        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());
        when(walletRepository.getById(transaction.getRecipientWalletId())).thenReturn(recipientWallet);
        when(walletTransactionService.createWalletTransaction(
                any(User.class),
                any(WalletToWalletTransaction.class),
                any(Wallet.class),
                any(Wallet.class)))
                .thenReturn(true);
        walletService.walletToWalletTransaction(user, 1, transaction);
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository, times(2)).update(walletCaptor.capture());

        List<Wallet> capturedWallets = walletCaptor.getAllValues();

        Wallet updatedSenderWallet = capturedWallets.get(0);
        Wallet updatedRecipientWallet = capturedWallets.get(1);

        assertEquals(100, recipientWallet.getBalance());
    }

    @Test
    void walletToWalletTransaction_FailedTransaction() {
        User user = new User();
        user.setId(1);
        Wallet senderWallet = new Wallet();
        senderWallet.setBalance(1000);
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();
        transaction.setRecipientWalletId(2);
        transaction.setAmount(100);

        doNothing().when(userService).isUserBlocked(user);
        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());
        when(walletTransactionService.createWalletTransaction(eq(user), eq(transaction), any(Wallet.class), any(Wallet.class))).thenReturn(false);

        walletService.walletToWalletTransaction(user, 1, transaction);

        assertEquals(1000, senderWallet.getBalance());
    }

    @Test
    void getByStringField_ReturnsCorrectWallet() {
        // Arrange
        String id = "walletId";
        String s = "testValue";
        Wallet expectedWallet = new Wallet();
        when(walletRepository.getByStringField(id, s)).thenReturn(expectedWallet);

        // Act
        Wallet resultWallet = walletService.getByStringField(id, s);

        // Assert
        assertEquals(expectedWallet, resultWallet);
        verify(walletRepository).getByStringField(id, s);
    }

    @Test
    void checkIbanExistence_ReturnsCorrectWallet() {
        // Arrange
        String ibanTo = "TEST_IBAN";
        Wallet expectedWallet = new Wallet();
        when(walletRepository.getByStringField("iban", ibanTo)).thenReturn(expectedWallet);

        // Act
        Wallet resultWallet = walletService.checkIbanExistence(ibanTo);

        // Assert
        assertEquals(expectedWallet, resultWallet);
        verify(walletRepository).getByStringField("iban", ibanTo);
    }

    @Test
    void chargeWallet_UpdatesBalanceCorrectly() {
        // Arrange
        wallet.setBalance(1000.0);
        double chargeAmount = 200.0;

        doNothing().when(walletRepository).update(any(Wallet.class));

        // Act
        walletService.chargeWallet(wallet, chargeAmount);

        // Assert
        assertEquals(800.0, wallet.getBalance());
        verify(walletRepository).update(wallet);
    }

    @Test
    void transferMoneyToRecipientWallet_UpdatesBalanceCorrectly() {
        // Arrange
        wallet.setBalance(500.0);
        double transferAmount = 200.0;


        doNothing().when(walletRepository).update(any(Wallet.class));

        // Act
        walletService.transferMoneyToRecipientWallet(wallet, transferAmount);

        // Assert
        assertEquals(700.0, wallet.getBalance());
        verify(walletRepository).update(wallet);
    }

    @Test
    void addUserToWallet_ThrowsUnauthorized_IfWalletTypeNotAllowed() {
        wallet.setWalletTypeId(1);
        wallet.setCreatedBy(2);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());

        Exception exception = assertThrows(UnauthorizedOperationException.class, () -> walletService.addUserToWallet(user, 1, 2));
        assertEquals(PERMISSIONS_ERROR_GENERAL, exception.getMessage());
    }

    @Test
    void addUserToWallet_ThrowsLimitReached_WhenMaxUsersAdded() {
        wallet.setWalletTypeId(2);
        wallet.setCreatedBy(1);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());

        when(walletRepository.getWalletUsers(1)).thenReturn(Collections.nCopies(5, new User()));

        Exception exception = assertThrows(LimitReachedException.class, () -> walletService.addUserToWallet(user, 1, 2));
        assertEquals(PERSONAL_ACCOUNTS_LIMIT_REACHED, exception.getMessage());
    }

    @Test
    void addUserToWallet_SuccessfullyAddsUser_WhenConditionsMet() {

        User newUser = new User();
        newUser.setId(2);
        Wallet wallet = new Wallet();
        wallet.setWalletTypeId(2);
        wallet.setCreatedBy(1);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());

        when(userService.verifyUserExistence(2)).thenReturn(newUser);
        when(walletRepository.getWalletUsers(1)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> walletService.addUserToWallet(user, 1, 2));
        verify(walletRepository).addUserToWallet(any(UserWallets.class));
    }

    @Test
    void addUserToWallet_ThrowsDuplicateEntityException_WhenUserAlreadyAdded() {
        // Arrange
        int existingUserId = 1;
        User existingUser = new User();
        existingUser.setId(existingUserId);
        wallet.setWalletTypeId(2);
        wallet.setCreatedBy(1);

        List<User> existingUsers = Arrays.asList(existingUser);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());
        when(walletRepository.getWalletUsers(1)).thenReturn(existingUsers);

        // Act & Assert
        Exception exception = assertThrows(DuplicateEntityException.class, () -> {
            walletService.addUserToWallet(existingUser, 1, existingUserId);
        });

        assertEquals("User,", exception.getMessage().split(" ")[0]);
        verify(walletRepository, never()).addUserToWallet(any(UserWallets.class));
    }

    @Test
    void removeUserFromWallet_ThrowsUnauthorized_IfWalletTypeNotAllowed() {
        wallet.setWalletTypeId(1);
        wallet.setCreatedBy(2);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());

        assertThrows(UnauthorizedOperationException.class, () -> walletService.removeUserFromWallet(user, 1, 2));
    }

    @Test
    void removeUserFromWallet_ThrowsUnauthorized_IfNotCreatedByUser() {

        wallet.setWalletTypeId(2);
        wallet.setCreatedBy(2);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());

        assertThrows(UnauthorizedOperationException.class, () -> walletService.removeUserFromWallet(user, 1, 3));
    }

    @Test
    void removeUserFromWallet_SuccessfullyRemovesUser_WhenConditionsMet() {
        User actingUser = new User();
        actingUser.setId(1);
        int walletId = 1;
        int userIdToRemove = 2;
        Wallet wallet = new Wallet();
        wallet.setWalletTypeId(2);
        wallet.setCreatedBy(1);
        User userToRemove = new User();
        userToRemove.setId(userIdToRemove);

        Mockito.doReturn(wallet).when(walletRepository).getById(anyInt());
        Mockito.doReturn(true).when(walletRepository).checkWalletOwnership(anyInt(), anyInt());
        when(userService.verifyUserExistence(userIdToRemove)).thenReturn(userToRemove);

        assertDoesNotThrow(() -> walletService.removeUserFromWallet(actingUser, walletId, userIdToRemove));
        verify(walletRepository).removeUserFromWallet(any(UserWallets.class));
    }

    @Test
    void getWalletUsers_ReturnsListOfUsers_WhenUserIsAuthorized() {
        // Arrange
        User user = new User();
        user.setId(1);
        int walletId = 123;
        List<User> expectedUsers = Arrays.asList(user, new User());

        when(walletRepository.getWalletUsers(walletId)).thenReturn(expectedUsers);

        // Act
        List<User> result = walletService.getWalletUsers(user, walletId);

        // Assert
        assertEquals(expectedUsers, result);
        verify(walletRepository).getWalletUsers(walletId);
    }

    @Test
    void getWalletUsers_ThrowsUnauthorizedOperationException_WhenUserIsNotAuthorized() {
        // Arrange
        User user = new User();
        user.setId(1);
        int walletId = 123;
        List<User> otherUsers = Arrays.asList(new User());

        when(walletRepository.getWalletUsers(walletId)).thenReturn(otherUsers);

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> {
            walletService.getWalletUsers(user, walletId);
        });
    }

    @Test
    void getUserCardTransactions_ReturnsTransactionsSuccessfully() {

        int walletId = 1;
        CardTransactionModelFilterOptions transactionFilter = new CardTransactionModelFilterOptions(null,
                null, null, null, null, null, null);
        List<CardToWalletTransaction> expectedTransactions = Arrays.asList(
                new CardToWalletTransaction(),
                new CardToWalletTransaction()
        );

        when(cardTransactionService.getUserCardTransactions(walletId, user, transactionFilter))
                .thenReturn(expectedTransactions);

        // Act
        List<CardToWalletTransaction> result = walletService.getUserCardTransactions(walletId, user, transactionFilter);

        // Assert
        assertEquals(expectedTransactions.size(), result.size());
        for (int i = 0; i < expectedTransactions.size(); i++) {
            assertEquals(expectedTransactions.get(i), result.get(i));
        }
        verify(cardTransactionService).getUserCardTransactions(walletId, user, transactionFilter);
    }

    @Test
    void getTransactionById_ReturnsTransaction_WhenUserIsOwner() {
        int walletId = 1;
        int transactionId = 100;
        WalletToWalletTransaction expectedTransaction = new WalletToWalletTransaction();

        doReturn(true).when(walletRepository).checkWalletOwnership(user.getId(), walletId);

        when(walletTransactionService.getWalletTransactionById(transactionId)).thenReturn(expectedTransaction);

        // Act
        WalletToWalletTransaction resultTransaction = walletService.getTransactionById(user, walletId, transactionId);

        // Assert
        assertEquals(expectedTransaction, resultTransaction);
    }

    @Test
    void getTransactionById_ThrowsUnauthorized_WhenUserIsNotOwner() {
        User user = new User();
        int walletId = 1;
        int transactionId = 100;

        doThrow(new UnauthorizedOperationException("Unauthorized")).when(walletRepository).checkWalletOwnership(user.getId(), walletId);

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> {
            walletService.getTransactionById(user, walletId, transactionId);
        });
    }

    @Test
    void verifyIfUserIsWalletOwner_ReturnsTrue_WhenUserIsOwner() {
        // Arrange
        user.setId(1);
        wallet.setCreatedBy(1);
        // Act
        boolean isOwner = walletService.verifyIfUserIsWalletOwner(user, wallet);

        // Assert
        assertTrue(isOwner);
    }

    @Test
    void verifyIfUserIsWalletOwner_ReturnsFalse_WhenUserIsNotOwner() {
        // Arrange
        user.setId(2);
        wallet.setCreatedBy(1);


        // Act
        boolean isOwner = walletService.verifyIfUserIsWalletOwner(user, wallet);

        // Assert
        assertFalse(isOwner);
    }

    @Test
    void verifyIfUserIsWalletOwnerWithException_ReturnsTrue_WhenUserIsOwner() {
        // Arrange
        user.setId(1);
        wallet.setCreatedBy(1);


        // Act & Assert
        assertDoesNotThrow(() -> {
            boolean isOwner = walletService.verifyIfUserIsWalletOwner(wallet, user);
            assertTrue(isOwner);
        });
    }

    @Test
    void verifyIfUserIsWalletOwner_ThrowsUnauthorizedOperationException_WhenUserIsNotOwner() {
        // Arrange
        user.setId(2);
        wallet.setCreatedBy(1);


        // Act & Assert
        UnauthorizedOperationException thrown = assertThrows(UnauthorizedOperationException.class, () -> {
            walletService.verifyIfUserIsWalletOwner(wallet, user);
        });

        assertEquals(UNAUTHORIZED_OPERATION_ERROR_MESSAGE, thrown.getMessage());
    }

    @Test
    void getWalletById_ThrowsUnauthorizedOperationException_WhenUserIsNotOwner() {
        // Arrange
        User user = new User();
        user.setId(1);
        int walletId = 123;

        when(walletRepository.checkWalletOwnership(user.getId(), walletId)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> {
            walletService.getWalletById(user, walletId);
        }, UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
    }

    @Test
    void createWallet_ThrowsDuplicateEntityException_WhenWalletNameExists() {
        // Arrange
        User user = new User();
        user.setId(1);
        Wallet existingWallet = new Wallet();
        existingWallet.setName("Existing Wallet Name");
        Set<Wallet> wallets = new HashSet<>();
        wallets.add(existingWallet);
        user.setWallets(wallets);

        Wallet newWallet = new Wallet();
        newWallet.setName("Existing Wallet Name");

        // Act & Assert
        assertThrows(DuplicateEntityException.class, () -> {
            walletService.createWallet(user, newWallet);
        });
    }

    @Test
    void createWallet_ThrowsLimitReachedException_WhenPersonalWalletLimitIsReached() {
        // Arrange


        Wallet personalWallet1 = new Wallet();
        personalWallet1.setWalletTypeId(1);
        Wallet personalWallet2 = new Wallet();
        personalWallet2.setWalletTypeId(1);
        Wallet personalWallet3 = new Wallet();
        personalWallet3.setWalletTypeId(1);
        Wallet personalWallet4 = new Wallet();
        personalWallet4.setWalletTypeId(1);
        personalWallet1.setName("1");
        personalWallet2.setName("2");
        personalWallet3.setName("3");
        personalWallet4.setName("4");

        Set<Wallet> wallets = new HashSet<>();
        wallets.add(personalWallet1);
        wallets.add(personalWallet2);
        wallets.add(personalWallet3);
        wallets.add(personalWallet4);

        user.setWallets(wallets);

        Wallet newPersonalWallet = new Wallet();
        newPersonalWallet.setWalletTypeId(1);

        // Act & Assert
        assertThrows(LimitReachedException.class, () -> {
            walletService.createWallet(user, newPersonalWallet);
        });
    }
}

