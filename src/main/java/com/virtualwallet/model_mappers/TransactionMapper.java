package com.virtualwallet.model_mappers;

import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.model_dto.CardTransactionDto;
import com.virtualwallet.models.model_dto.TransactionDto;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.services.contracts.StatusService;
import com.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.virtualwallet.model_helpers.ModelConstantHelper.PENDING_TRANSACTION_ID;

@Component
public class TransactionMapper {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final StatusService statusService;

    @Autowired
    public TransactionMapper(WalletService walletService,
                             UserRepository userRepository,
                             StatusService statusService) {
        this.walletService = walletService;
        this.userRepository = userRepository;
        this.statusService = statusService;
    }

    public WalletToWalletTransaction fromDto(TransactionDto transactionDto) {
        WalletToWalletTransaction walletToWalletTransaction = new WalletToWalletTransaction();
        walletToWalletTransaction.setAmount(transactionDto.getAmount());
        walletToWalletTransaction.setUserId(transactionDto.getUserId());
        walletToWalletTransaction.setRecipientWalletId(transactionDto.getRecipientWalletId());
        walletToWalletTransaction.setStatus(statusService.getStatus(PENDING_TRANSACTION_ID));
        walletToWalletTransaction.setTime(LocalDateTime.now());
        return walletToWalletTransaction;
    }

    public WalletToWalletTransaction fromDto(TransactionDto transactionDto, int id) {
        WalletToWalletTransaction walletToWalletTransaction = new WalletToWalletTransaction();
        walletToWalletTransaction.setTransactionId(id);
        walletToWalletTransaction.setAmount(transactionDto.getAmount());
        walletToWalletTransaction.setUserId(transactionDto.getUserId());
        walletToWalletTransaction.setRecipientWalletId(transactionDto.getRecipientWalletId());
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
