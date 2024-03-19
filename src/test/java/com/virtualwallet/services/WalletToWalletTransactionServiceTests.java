package com.virtualwallet.services;

import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.Status;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.repositories.contracts.WalletToWalletTransactionRepository;
import com.virtualwallet.services.contracts.StatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.virtualwallet.model_helpers.ModelConstantHelper.CONFIRMED_TRANSACTION_ID;
import static com.virtualwallet.model_helpers.ModelConstantHelper.DECLINED_TRANSACTION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletToWalletTransactionServiceTests {
    @Mock
    private WalletToWalletTransactionRepository walletTransactionRepository;

    @Mock
    private StatusService statusService;

    @InjectMocks
    private WalletToWalletTransactionServiceImpl walletTransactionService;

    @Test
    void getWalletTransactionById_ReturnsCorrectTransaction() {
        // Arrange
        int transactionId = 1;
        WalletToWalletTransaction expectedTransaction = new WalletToWalletTransaction();
        when(walletTransactionRepository.getById(transactionId)).thenReturn(expectedTransaction);

        // Act
        WalletToWalletTransaction result = walletTransactionService.getWalletTransactionById(transactionId);

        // Assert
        assertEquals(expectedTransaction, result);
        verify(walletTransactionRepository).getById(transactionId);
    }

    @Test
    void createWalletTransaction_CreatesTransactionWithoutAdmin_WhenAmountIsLow() {
        // Arrange
        User user = new User();
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();
        transaction.setAmount(5000);
        Wallet senderWallet = mock(Wallet.class);
        Wallet recipientWallet = mock(Wallet.class);

        Set<WalletToWalletTransaction> senderTransactions = new HashSet<>();
        Set<WalletToWalletTransaction> recipientTransactions = new HashSet<>();

        when(senderWallet.getWalletTransactions()).thenReturn(senderTransactions);
        when(recipientWallet.getWalletTransactions()).thenReturn(recipientTransactions);

        when(statusService.getStatus(CONFIRMED_TRANSACTION_ID)).thenReturn(new Status(CONFIRMED_TRANSACTION_ID, "Confirmed"));

        // Act
        boolean result = walletTransactionService.createWalletTransaction(user, transaction, senderWallet, recipientWallet);

        // Assert
        assertTrue(result);
        verify(walletTransactionRepository, times(2)).create(any(WalletToWalletTransaction.class));
        assertEquals("Confirmed", transaction.getStatus().getName());
    }


    @Test
    void approveTransaction_UpdatesAndCreatesIncomingTransaction() {
        // Arrange
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();
        Wallet recipientWallet = mock(Wallet.class);
        Set<WalletToWalletTransaction> transactions = new HashSet<>();

        Status confirmedStatus = new Status(CONFIRMED_TRANSACTION_ID, "Confirmed");
        when(statusService.getStatus(CONFIRMED_TRANSACTION_ID)).thenReturn(confirmedStatus);
        when(recipientWallet.getWalletTransactions()).thenReturn(transactions);

        // Act
        walletTransactionService.approveTransaction(transaction, recipientWallet);

        // Assert
        assertEquals("Confirmed", transaction.getStatus().getName());
        verify(walletTransactionRepository).update(transaction);
        verify(walletTransactionRepository).create(any(WalletToWalletTransaction.class));

    }

    @Test
    void getAllWalletTransactionsWithFilter_DelegatesToRepository() {
        // Arrange
        User user = new User();
        WalletTransactionModelFilterOptions transactionFilter = mock(WalletTransactionModelFilterOptions.class);
        List<WalletToWalletTransaction> expectedTransactions = Arrays.asList(new WalletToWalletTransaction(), new WalletToWalletTransaction());

        when(walletTransactionRepository.getAllWalletTransactionsWithFilter(user, transactionFilter)).thenReturn(expectedTransactions);

        // Act
        List<WalletToWalletTransaction> result = walletTransactionService.getAllWalletTransactionsWithFilter(user, transactionFilter);

        // Assert
        assertEquals(expectedTransactions, result);
        verify(walletTransactionRepository).getAllWalletTransactionsWithFilter(user, transactionFilter);
    }

    @Test
    void getUserWalletTransactions_DelegatesToRepository() {
        // Arrange
        User user = new User();
        WalletTransactionModelFilterOptions transactionFilter = mock(WalletTransactionModelFilterOptions.class);
        int walletId = 123;
        List<WalletToWalletTransaction> expectedTransactions = Arrays.asList(new WalletToWalletTransaction(), new WalletToWalletTransaction());

        when(walletTransactionRepository.getUserWalletTransactions(user, transactionFilter, walletId)).thenReturn(expectedTransactions);

        // Act
        List<WalletToWalletTransaction> result = walletTransactionService.getUserWalletTransactions(user, transactionFilter, walletId);

        // Assert
        assertEquals(expectedTransactions, result);
        verify(walletTransactionRepository).getUserWalletTransactions(user, transactionFilter, walletId);
    }

    @Test
    void cancelTransaction_UpdatesTransactionStatusToDeclined() {
        // Arrange
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();
        Status declinedStatus = new Status(DECLINED_TRANSACTION_ID, "Declined");
        when(statusService.getStatus(DECLINED_TRANSACTION_ID)).thenReturn(declinedStatus);

        // Act
        walletTransactionService.cancelTransaction(transaction);

        // Assert
        assertEquals(declinedStatus, transaction.getStatus());
        verify(walletTransactionRepository).update(transaction);
    }

}
