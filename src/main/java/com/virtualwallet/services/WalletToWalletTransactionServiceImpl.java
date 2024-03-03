package com.virtualwallet.services;

import com.virtualwallet.model_helpers.TransactionModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.services.contracts.StatusService;
import com.virtualwallet.services.contracts.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

@Service
public class WalletToWalletTransactionServiceImpl implements WalletTransactionService {
    private final walletToWalletTransactionRepository walletTransactionRepository;
    private final StatusService statusService;

    @Autowired
    public WalletToWalletTransactionServiceImpl(walletToWalletTransactionRepository walletTransactionRepository,
                                                StatusService statusService) {
        this.walletToWalletTransactionRepository = walletTransactionRepository;
        this.statusService = statusService;
    }

    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactions() {
        return walletTransactionRepository.getAll();
    }

    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (User user, TransactionModelFilterOptions transactionFilter) {

        return walletTransactionRepository.getAllWalletTransactionsWithFilter(user, transactionFilter);
    }

    @Override
    public List<WalletToWalletTransaction> getUserWalletTransactions(Wallet wallet) {
        return walletTransactionRepository.getUserWalletTransactions(wallet);
    }

    @Override
    public WalletToWalletTransaction getWalletTransactionById(int walletTransactionId) {
        return walletTransactionRepository.getById(walletTransactionId);
    }

    /**
     * Returns a boolean depending on the transaction's request transfer amount. In both cases a transaction is created
     * with the difference of its status, which also determines whether the recipient will receive an incoming transaction.
     *
     * @param
     * @param transaction
     * @return true If the transfer amount is less than 10 000 the transaction is deemed successful
     * and both sender and recipient will
     * have a transaction created in their respective wallets. This in turn will affect both of their wallets' balance.<br/>
     * false If the transfer amount is over 10 000 only a single transaction will be created for the sender with status pending
     */
    @Override
    public boolean createWalletTransaction(User user, WalletToWalletTransaction transaction) {
        //set outgoing transaction type
        transaction.setTransactionTypeId(OUTGOING_TRANSACTION_TYPE_ID);
        if (!doesTransactionRequireAdminAction(transaction)) {


            walletTransactionRepository.create(transaction);

            //create incoming transaction
            WalletToWalletTransaction walletToWalletTransactionIncoming = new WalletToWalletTransaction();
            doIncomingTransaction(walletToWalletTransactionIncoming, transaction);
            walletTransactionRepository.create(walletToWalletTransactionIncoming);
            return true;
        }
        walletTransactionRepository.create(transaction);
        return false;
    }

    //todo think about whether the second transaction would still need to be created just with pending status
    @Override
    public void approveTransaction(WalletToWalletTransaction transaction) {
        transaction.setStatus(statusService.getStatus(CONFIRMED_TRANSACTION_ID));
        walletTransactionRepository.update(transaction);
        //create incoming transaction
        WalletToWalletTransaction walletToWalletTransactionIncoming = new WalletToWalletTransaction();
        doIncomingTransaction(walletToWalletTransactionIncoming, transaction);
        walletTransactionRepository.create(walletToWalletTransactionIncoming);
    }

    @Override
    public void cancelTransaction(WalletToWalletTransaction transaction) {
        transaction.setStatus(statusService.getStatus(DECLINED_TRANSACTION_ID));
        walletTransactionRepository.update(transaction);
    }

    private boolean doesTransactionRequireAdminAction(WalletToWalletTransaction walletToWalletTransaction) {
        if (walletToWalletTransaction.getAmount() >= MAX_TRANSACTION_AMOUNT) {
            return true;
        }
        walletToWalletTransaction.setStatus(statusService.getStatus(CONFIRMED_TRANSACTION_ID));
        return false;
    }

    private void doIncomingTransaction(WalletToWalletTransaction walletToWalletTransactionIncoming,
                                       WalletToWalletTransaction transactionFrom) {
        walletToWalletTransactionIncoming.setAmount(transactionFrom.getAmount());
        walletToWalletTransactionIncoming.setTime(LocalDateTime.now());
        walletToWalletTransactionIncoming.setTransactionTypeId(INCOMING_TRANSACTION_TYPE_ID);
        walletToWalletTransactionIncoming.setUserId(transactionFrom.getUserId());
        walletToWalletTransactionIncoming.setStatus(statusService.getStatus(CONFIRMED_TRANSACTION_ID));
        walletToWalletTransactionIncoming
                .setRecipientWalletId(walletRepository.getById
                        (transactionFrom.getRecipientWalletId()).getWalletId());
    }
}