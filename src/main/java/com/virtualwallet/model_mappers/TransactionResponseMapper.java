package com.virtualwallet.model_mappers;

import com.virtualwallet.models.Transaction;
import com.virtualwallet.models.model_dto.TransactionResponseDto;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.repositories.contracts.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionResponseMapper {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public TransactionResponseMapper(UserRepository userRepository,
                                     WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    public TransactionResponseDto convertToDto(Transaction transaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setTransactionTypeId(transaction.getTransactionTypeId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setAmount(transaction.getAmount());
        dto.setUserName(userRepository.getById(transaction.getUserId()).getUsername());
        dto.setWalletIban(walletRepository.getById(transaction.getWalletId()).getIban());
        dto.setTime(transaction.getTime());
        return dto;
    }

    public TransactionResponseDto convertToDto(Transaction transaction, int id) {
        TransactionResponseDto dto = new TransactionResponseDto();

        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionId(id);
        dto.setAmount(transaction.getAmount());
        dto.setUserName(userRepository.getById(transaction.getUserId()).getUsername());
        dto.setWalletIban(walletRepository.get(transaction.getWalletId()).getIban());
        dto.setTime(transaction.getTime());
        return dto;

    }

}
