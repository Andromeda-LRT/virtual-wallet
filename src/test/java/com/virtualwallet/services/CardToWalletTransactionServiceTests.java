package com.virtualwallet.services;

import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.*;
import com.virtualwallet.repositories.contracts.CardToWalletTransactionRepository;
import com.virtualwallet.services.contracts.StatusService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.CONFIRMED_TRANSACTION_ID;
import static com.virtualwallet.model_helpers.ModelConstantHelper.DECLINED_TRANSACTION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardToWalletTransactionServiceTests {
    @Mock
    private CardToWalletTransactionRepository cardTransactionRepository;

    @Mock
    private StatusService statusService;

    @InjectMocks
    private CardToWalletTransactionServiceImpl cardToWalletTransactionService;

    @Test
    void getAllCardTransactions_DelegatesToRepository() {
        // When
        cardToWalletTransactionService.getAllCardTransactions();

        // Then
        Mockito.verify(cardTransactionRepository, Mockito.times(1)).getAll();
    }

    @Test
    void etAllCardTransactionsWithFilter_DelegatesToRepository() {
        // Arrange
        User user = new User();
        CardTransactionModelFilterOptions transactionFilter = mock(CardTransactionModelFilterOptions.class);
        List<CardToWalletTransaction> expectedTransactions = Arrays.asList(new CardToWalletTransaction(), new CardToWalletTransaction());

        Mockito.when(cardTransactionRepository.getAllCardTransactionsWithFilter(user, transactionFilter)).thenReturn(expectedTransactions);

        // Act
        List<CardToWalletTransaction> result = cardToWalletTransactionService.getAllCardTransactionsWithFilter(user, transactionFilter);

        // Assert
        Assertions.assertEquals(expectedTransactions, result);
        Mockito.verify(cardTransactionRepository).getAllCardTransactionsWithFilter(user, transactionFilter);
    }

    @Test
    void getUserCardTransactions_DelegatesToRepository() {
        // Arrange
        User user = new User();
        int walletId = 123;
        CardTransactionModelFilterOptions transactionFilter = mock(CardTransactionModelFilterOptions.class);

        List<CardToWalletTransaction> expectedTransactions = Arrays.asList(new CardToWalletTransaction(), new CardToWalletTransaction());

        Mockito.when(cardTransactionRepository.getAllUserCardTransactions(walletId, user, transactionFilter)).thenReturn(expectedTransactions);

        // Act
        List<CardToWalletTransaction> result = cardTransactionRepository.getAllUserCardTransactions(walletId, user, transactionFilter);

        // Assert
        Assertions.assertEquals(expectedTransactions, result);
        Mockito.verify(cardTransactionRepository).getAllUserCardTransactions(walletId, user, transactionFilter);
    }

    @Test
    void declineTransaction_UpdatesTransactionStatusToDeclined() {
        // Arrange
        CardToWalletTransaction transaction = new CardToWalletTransaction();
        Status declinedStatus = new Status(DECLINED_TRANSACTION_ID, "Declined");
        Card card = mock(Card.class);
        Wallet wallet = mock(Wallet.class);
        Mockito.when(statusService.getStatus(DECLINED_TRANSACTION_ID)).thenReturn(declinedStatus);

        // Act
        cardToWalletTransactionService.declineTransaction(transaction, new User(), card, wallet);

        // Assert
        Assertions.assertEquals(declinedStatus, transaction.getStatus());
        Mockito.verify(cardTransactionRepository).create(transaction);
    }

    @Test
    void approveTransaction_UpdatesTransactionStatusToConfirmed() {
        // Arrange
        CardToWalletTransaction transaction = new CardToWalletTransaction();
        Status confirmedStatus = new Status(CONFIRMED_TRANSACTION_ID, "Confirmed");
        Card card = mock(Card.class);
        Wallet wallet = mock(Wallet.class);
        Mockito.when(statusService.getStatus(CONFIRMED_TRANSACTION_ID)).thenReturn(confirmedStatus);

        // Act
        cardToWalletTransactionService.approveTransaction(transaction, new User(), card, wallet);

        // Assert
        Assertions.assertEquals(confirmedStatus, transaction.getStatus());
        Mockito.verify(cardTransactionRepository).create(transaction);
    }

    @Test
    void getCardTransactionById_DelegatesToRepository() {
        // Arrange
        int cardTransactionId = 1;
        CardToWalletTransaction expectedTransaction = new CardToWalletTransaction();
        Mockito.when(cardTransactionRepository.get(cardTransactionId)).thenReturn(expectedTransaction);

        // Act
        CardToWalletTransaction result = cardToWalletTransactionService.getCardTransactionById(cardTransactionId);

        // Assert
        Assertions.assertEquals(expectedTransaction, result);
        Mockito.verify(cardTransactionRepository).get(cardTransactionId);
    }

    @Test
    void updateCardTransaction_DelegatesToRepository() {
        // Arrange
        CardToWalletTransaction cardTransaction = new CardToWalletTransaction();
        User user = new User();

        // Act
        cardToWalletTransactionService.updateCardTransaction(cardTransaction, user);

        // Assert
        Mockito.verify(cardTransactionRepository).update(cardTransaction);
    }

}
