package com.virtualwallet.services;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Transaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.repositories.contracts.WalletRepository;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import com.virtualwallet.utils.UtilHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {
    private final Double MAX_TRANSACTION_AMOUNT = 10000.0;
    private final WalletRepository walletRepository;
    private UtilHelpers utilHelpers;

    private final UserService userService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, UserService userService) {
        this.walletRepository = walletRepository;
        this.userService = userService;
    }

    @Override
    public List<Wallet> getAllWallets(User user, int user_id) {
        utilHelpers.verifyUserAccess(user_id, user);
        return walletRepository.getAllWallets(user_id);
    }

    @Override
    public Wallet getWalletById(User user, int user_id, int wallet_id) {
        utilHelpers.verifyUserAccess(user_id, user);
        checkWalletOwnership(user, wallet_id);
        //todo check if user is part of this wallet - LYUBIMA
        // checkUserPartOfWallet(user_id, wallet_id);
        return walletRepository.getWalletById(wallet_id);
    }

    @Override
    public Wallet createWallet(User user, Wallet wallet) {
        return walletRepository.createWallet(user.getId(), wallet);
    }

    @Override
    public Wallet updateWallet(User user, Wallet wallet) {
        checkWalletExistence(wallet.getWalletId());
        checkWalletOwnership(user, wallet.getWalletId());
        // if above method does not throw exception, then user is part of wallet
        // if above method throw exception then
        // checkUserPartOfWallet(user.getId(), wallet.getWalletId());
        // if this method does not throw exception, then user is part of wallet
        return walletRepository.updateWallet(user.getId(), wallet);
    }

    @Override
    public void delete(User user, int user_id, int wallet_id) {
        utilHelpers.verifyUserAccess(user_id, user);
        checkWalletExistence(wallet_id);
        checkWalletOwnership(user, wallet_id);
        walletRepository.delete(user_id, wallet_id);
    }

    @Override
    public List<Transaction> getAllTransactions(User user, int user_id, int wallet_id) {
        userService.get(user_id, user);
        checkWalletOwnership(user, wallet_id);
        return walletRepository.getAllTransactions(wallet_id);
    }

    @Override
    public Transaction getTransactionById(User user, int wallet_id, int transaction_id) {
        checkWalletExistence(wallet_id);
        checkWalletOwnership(user, wallet_id);
        return transactionRepository.getTransactionById(wallet_id, transaction_id);
    }

    @Override
    public Transaction createTransaction(User user, Transaction transaction, int wallet_id) {
        checkWalletOwnership(user, wallet_id);
        transaction.setTime(LocalDateTime.now());

        if (transaction.getAmount() >= MAX_TRANSACTION_AMOUNT) {
            transaction.setStatus("pending");
        }

        return transactionRepository.createTransaction(user, transaction, wallet_id);
    }

    @Override
    public Transaction updateTransaction(User user, Transaction transaction, int wallet_id) {
        checkWalletExistence(wallet_id);
        checkWalletOwnership(user, wallet_id);
        if (transaction.getAmount() >= MAX_TRANSACTION_AMOUNT) {
            transaction.setStatus("pending");
        }
        return transactionRepository.updateTransaction(user, transaction, wallet_id);
    }

    @Override
    public void approveTransaction(User user, int transaction_id, int wallet_id) {
        //TODO TO be implemented - LYUBIMA
        checkWalletExistence(wallet_id);
        checkWalletOwnership(user, wallet_id);
        User userToCheck = userService.get(user.getId(), user);
        if (userToCheck.getRole().getName().equals("admin")) {
            transactionRepository.approveTransaction(user, transaction_id, wallet_id);
        }
    }

    @Override
    public void cancelTransaction(User user, int transaction_id, int wallet_id) {
        //TODO TO be implemented - LYUBIMA
    }

    @Override
    public void transactionWithCard(User user, int card_id, int wallet_id) {
        //TODO TO be implemented - LYUBIMA
    }

//    private void checkUserPartOfWallet(int user_id, int wallet_id) {
//        // TODO this method should throw exception if user is not part of wallet - LYUBIMA
//        if (!walletRepository.checkIfUserIsPartOfWallet(user_id, wallet_id)) {
//            throw new UnauthorizedOperationException("You are not part of this wallet");
//        }
//    }

    private void checkWalletOwnership(User user, int wallet_id) {
        // todo this method should throw exception if wallet is not owned by user - LYUBIMA
        if (!walletRepository.checkWalletOwnership(user.getId(), wallet_id) && !userService.verifyAdminAccess(user)) {
            throw new UnauthorizedOperationException("You are not authorized for this operation");
        }
    }

    private void checkWalletExistence(int wallet_id) {
        if (walletRepository.getWalletById(wallet_id) == null) {
            throw new EntityNotFoundException("Wallet does not exist");
        }
    }
}
