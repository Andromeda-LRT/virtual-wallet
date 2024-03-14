package com.virtualwallet.model_mappers;

import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.response_model_dto.TransactionResponseDto;
import com.virtualwallet.repositories.contracts.CardRepository;
import com.virtualwallet.repositories.contracts.UserRepository;
import com.virtualwallet.repositories.contracts.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionResponseMapper {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public TransactionResponseMapper(UserRepository userRepository, CardRepository cardRepository,
                                     WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.walletRepository = walletRepository;
    }

    public TransactionResponseDto convertToDto(WalletToWalletTransaction walletToWalletTransaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setTransactionType(walletToWalletTransaction.getTransactionTypeId() == 1 ? "Incoming" : "Outgoing");
        dto.setTransactionId(walletToWalletTransaction.getTransactionId());
        dto.setAmount(walletToWalletTransaction.getAmount());
        dto.setSender(walletToWalletTransaction.getSender().getUsername());
        dto.setRecipient(walletRepository.getById(walletToWalletTransaction.getRecipientWalletId()).getIban());
        dto.setTime(walletToWalletTransaction.getTime());
        dto.setStatus(walletToWalletTransaction.getStatus().getName());
        return dto;
    }

    public TransactionResponseDto convertToDto(CardToWalletTransaction cardToWalletTransaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setTransactionType(cardToWalletTransaction.getTransactionTypeId() == 1 ? "Incoming" : "Outgoing");
        dto.setTransactionId(cardToWalletTransaction.getTransactionId());
        dto.setAmount(cardToWalletTransaction.getAmount());
        dto.setSender("**".concat(cardRepository.getById(cardToWalletTransaction.getCardId()).getNumber().substring(12, 16)));
        dto.setRecipient(walletRepository.getById(cardToWalletTransaction.getWalletId()).getIban());
        dto.setTime(cardToWalletTransaction.getTime());
        dto.setStatus(cardToWalletTransaction.getStatus().getName());
        return dto;
    }

    public TransactionResponseDto convertToDto(WalletToWalletTransaction walletToWalletTransaction, int id) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setTransactionType(walletToWalletTransaction.getTransactionTypeId() == 1 ? "Incoming" : "Outgoing");
        dto.setTransactionId(walletToWalletTransaction.getTransactionId());
        dto.setTransactionId(id);
        dto.setAmount(walletToWalletTransaction.getAmount());
        dto.setSender(walletToWalletTransaction.getSender().getUsername());
        dto.setRecipient(walletRepository.getById(walletToWalletTransaction.getRecipientWalletId()).getIban());
        dto.setTime(walletToWalletTransaction.getTime());
        dto.setStatus(walletToWalletTransaction.getStatus().getName());
        return dto;
    }

    public List<TransactionResponseDto> convertWalletTransactionsToDto(List<WalletToWalletTransaction> walletToWalletTransactions) {
        List<TransactionResponseDto> transactionResponseDtos = new ArrayList<>();
        for (WalletToWalletTransaction walletToWalletTransaction : walletToWalletTransactions) {
            transactionResponseDtos.add(convertToDto(walletToWalletTransaction));
        }
        return transactionResponseDtos;
    }

    public List<TransactionResponseDto> convertCardTransactionsToDto(List<CardToWalletTransaction> cardToWalletTransactions) {
        List<TransactionResponseDto> transactionResponseDtos = new ArrayList<>();
        for (CardToWalletTransaction cardToWalletTransaction : cardToWalletTransactions) {
            transactionResponseDtos.add(convertToDto(cardToWalletTransaction));
        }
        return transactionResponseDtos;
    }
}
