package com.virtualwallet.services;

import com.virtualwallet.exceptions.InsufficientFundsException;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.Status;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.services.contracts.CardTransactionService;
import com.virtualwallet.services.contracts.WalletService;
import com.virtualwallet.services.contracts.WalletTransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.relation.Role;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IntermediateTransactionServiceTests {

    @Mock
    private WalletService walletService;

    @Mock
    private CardTransactionService cardTransactionService;

    @Mock
    private WalletTransactionService walletTransactionService;

    @InjectMocks
    private IntermediateTransactionServiceImpl intermediateTransactionService;

    @Test
    void getAllWithFilter_CallsWalletTransactionService_WhenUserIsAdmin() {
        // Arrange
        User adminUser = mock(User.class);
        Role adminRole = mock(Role.class);
        when(adminUser.getRole()).thenReturn(new com.virtualwallet.models.Role(1, "admin"));
        WalletTransactionModelFilterOptions filterOptions = mock(WalletTransactionModelFilterOptions.class);

        // Act
        intermediateTransactionService.getAllWithFilter(adminUser, filterOptions);

        // Assert
        verify(walletTransactionService).getAllWalletTransactionsWithFilter(adminUser, filterOptions);
    }

    @Test
    void getAllCardTransactionsWithFilter_CallsCardTransactionService_WhenUserIsAdmin() {
        // Arrange
        User adminUser = mock(User.class);
        when(adminUser.getRole()).thenReturn(new com.virtualwallet.models.Role(1, "admin"));

        CardTransactionModelFilterOptions filterOptions = mock(CardTransactionModelFilterOptions.class);

        // Act
        intermediateTransactionService.getAllCardTransactionsWithFilter(adminUser, filterOptions);

        // Assert
        verify(cardTransactionService).getAllCardTransactionsWithFilter(adminUser, filterOptions);
    }

    @Test
    void approveTransaction_ApprovesAndTransfersAmount_WhenConditionsMet() {
        // Arrange
        User user = new User();
        int transactionId = 1;
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();
        transaction.setStatus(new Status(1, "Pending"));
        transaction.setAmount(100);
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        when(walletTransactionService.getWalletTransactionById(transactionId)).thenReturn(transaction);
        when(walletService.getWalletById(user, transaction.getWalletId())).thenReturn(senderWallet);
        when(walletService.getWalletById(user, transaction.getRecipientWalletId())).thenReturn(recipientWallet);
        doNothing().when(walletService).checkWalletBalance(senderWallet, transaction.getAmount());

        // Act
        intermediateTransactionService.approveTransaction(user, transactionId);

        // Assert
        verify(walletTransactionService).approveTransaction(transaction, recipientWallet);
        verify(walletService).chargeWallet(senderWallet, transaction.getAmount());
        verify(walletService).transferMoneyToRecipientWallet(recipientWallet, transaction.getAmount());
    }

    @Test
    void approveTransaction_CancelsTransaction_WhenInsufficientFunds() {
        // Arrange
        User user = new User();
        int transactionId = 1;
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();
        transaction.setStatus(new Status(1, "Pending"));
        transaction.setAmount(100);
        Wallet senderWallet = new Wallet();
        senderWallet.setBalance(0);

        when(walletTransactionService.getWalletTransactionById(transactionId)).thenReturn(transaction);
        when(walletService.getWalletById(user, transaction.getWalletId())).thenReturn(senderWallet);
        doThrow(InsufficientFundsException.class).when(walletService).checkWalletBalance(senderWallet, transaction.getAmount());

        // Act
        intermediateTransactionService.approveTransaction(user, transactionId);

        // Assert
        verify(walletTransactionService).cancelTransaction(transaction);
    }

    @Test
    void cancelTransaction_CancelsTransactionSuccessfully() {
        // Arrange
        int transactionId = 1;
        WalletToWalletTransaction transaction = new WalletToWalletTransaction();

        Status transactionStatus = mock(Status.class);
        when(transactionStatus.getName()).thenReturn("Pending");
        transaction.setStatus(transactionStatus);

        when(walletTransactionService.getWalletTransactionById(transactionId)).thenReturn(transaction);

        // Act
        intermediateTransactionService.cancelTransaction(new User(), transactionId);

        // Assert
        verify(walletTransactionService).cancelTransaction(transaction);
    }

}
