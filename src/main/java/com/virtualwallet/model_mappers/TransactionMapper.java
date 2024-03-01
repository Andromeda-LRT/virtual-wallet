package com.virtualwallet.model_mappers;

import com.virtualwallet.models.Transaction;
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

    public Transaction fromDto(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setUserId(transactionDto.getUserId());
        transaction.setRecipientWalletId(transactionDto.getRecipientWalletId());
        transaction.setTime(LocalDateTime.now());
        return transaction;
    }

    public Transaction fromDto(TransactionDto transactionDto, int id) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(id);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setUserId(transactionDto.getUserId());
        transaction.setRecipientWalletId(transactionDto.getRecipientWalletId());
        return transaction;
    }
}
