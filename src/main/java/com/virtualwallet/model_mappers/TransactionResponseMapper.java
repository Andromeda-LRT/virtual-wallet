package com.virtualwallet.model_mappers;

import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.model_dto.TransactionResponseDto;
import com.virtualwallet.models.model_dto.UserDto;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.repositories.contracts.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    public TransactionResponseDto convertToDto(WalletToWalletTransaction walletToWalletTransaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setTransactionTypeId(walletToWalletTransaction.getTransactionTypeId());
        dto.setTransactionId(walletToWalletTransaction.getTransactionId());
        dto.setAmount(walletToWalletTransaction.getAmount());
        dto.setUserName(userRepository.getById(walletToWalletTransaction.getUserId()).getUsername());
        dto.setWalletIban(walletRepository.getById(walletToWalletTransaction.getWalletId()).getIban());
        dto.setTime(walletToWalletTransaction.getTime());
        return dto;
    }

    public TransactionResponseDto convertToDto(WalletToWalletTransaction walletToWalletTransaction, int id) {
        TransactionResponseDto dto = new TransactionResponseDto();

        dto.setTransactionId(walletToWalletTransaction.getTransactionId());
        dto.setTransactionId(id);
        dto.setAmount(walletToWalletTransaction.getAmount());
        dto.setUserName(userRepository.getById(walletToWalletTransaction.getUserId()).getUsername());
        dto.setWalletIban(walletRepository.getById(walletToWalletTransaction.getWalletId()).getIban());
        dto.setTime(walletToWalletTransaction.getTime());
        return dto;

    }

    public List<TransactionResponseDto> convertToDto(List<WalletToWalletTransaction> walletToWalletTransactions, int id ) {
        List<TransactionResponseDto> transactionResponseDtos = new ArrayList<>();
        for (WalletToWalletTransaction walletToWalletTransaction : walletToWalletTransactions) {
            transactionResponseDtos.add(convertToDto(walletToWalletTransaction, id));
        }
        return transactionResponseDtos;
    }
}
