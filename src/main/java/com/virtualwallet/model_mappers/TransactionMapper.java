package com.virtualwallet.model_mappers;

import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.model_dto.TransactionDto;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    private final WalletService walletService;
    private final UserRepository userRepository;

    @Autowired
    public TransactionMapper(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    public WalletToWalletTransaction fromDto(TransactionDto transactionDto) {
        WalletToWalletTransaction walletToWalletTransaction = new WalletToWalletTransaction();
        walletToWalletTransaction.setAmount(transactionDto.getAmount());
        walletToWalletTransaction.setUserId(transactionDto.getUserId());
        walletToWalletTransaction.setRecipientWalletId(transactionDto.getRecipientWalletId());
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
}
