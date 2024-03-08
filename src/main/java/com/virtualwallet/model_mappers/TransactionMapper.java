package com.virtualwallet.model_mappers;

import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.model_dto.CardTransactionDto;
import com.virtualwallet.models.model_dto.TransactionDto;
import com.virtualwallet.services.contracts.StatusService;
import com.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.virtualwallet.model_helpers.ModelConstantHelper.PENDING_TRANSACTION_ID;

@Component
public class TransactionMapper {

    private final WalletService walletService;
    private final StatusService statusService;

    @Autowired
    public TransactionMapper(WalletService walletService,
                             StatusService statusService) {
        this.walletService = walletService;
        this.statusService = statusService;
    }

    public WalletToWalletTransaction fromDto(TransactionDto transactionDto, User user, int walletId) {
        WalletToWalletTransaction walletToWalletTransaction = new WalletToWalletTransaction();

        walletToWalletTransaction.setAmount(transactionDto.getAmount());
        walletToWalletTransaction.setSender(user);
        walletToWalletTransaction.setStatus(statusService.getStatus(PENDING_TRANSACTION_ID));
        walletToWalletTransaction.setRecipientWalletId(walletService.checkIbanExistence(transactionDto.getIban()).getWalletId());
        walletToWalletTransaction.setTime(LocalDateTime.now());
        walletToWalletTransaction.setWalletId(walletId);

        return walletToWalletTransaction;
    }

    public WalletToWalletTransaction fromDto(TransactionDto transactionDto, int id, User user) {
        WalletToWalletTransaction walletToWalletTransaction = new WalletToWalletTransaction();
        walletToWalletTransaction.setTransactionId(id);
        walletToWalletTransaction.setAmount(transactionDto.getAmount());
        walletToWalletTransaction.setSender(user);
        walletToWalletTransaction.setRecipientWalletId(walletService.checkIbanExistence(transactionDto.getIban()).getWalletId());
        return walletToWalletTransaction;
    }

    public CardToWalletTransaction fromDto(CardTransactionDto transactionDto) {
        CardToWalletTransaction cardToWalletTransaction = new CardToWalletTransaction();
        cardToWalletTransaction.setAmount(transactionDto.getAmount());
        cardToWalletTransaction.setStatus(statusService.getStatus(PENDING_TRANSACTION_ID));
        cardToWalletTransaction.setTime(LocalDateTime.now());
        return cardToWalletTransaction;
    }


}
