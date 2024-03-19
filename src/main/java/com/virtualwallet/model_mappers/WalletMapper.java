package com.virtualwallet.model_mappers;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.input_model_dto.WalletDto;
import com.virtualwallet.models.response_model_dto.WalletResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.virtualwallet.services.contracts.WalletService;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;
import static com.virtualwallet.utils.IBANGenerator.generateRandomIBAN;

@Component
public class WalletMapper {

    private final WalletService walletService;

    @Autowired
    public WalletMapper(WalletService walletService) {
        this.walletService = walletService;
    }

    public Wallet fromDto(WalletDto walletDto){
        Wallet wallet = new Wallet();
        wallet.setName(walletDto.getName());
        wallet.setIban(setUniqueIban());
        wallet.setWalletTypeId(walletDto.getWalletTypeId());
        return wallet;
    }

    public Wallet fromDto(WalletDto walletDto, int id, User user){
        Wallet wallet = walletService.getWalletById(user, id);
        wallet.setName(walletDto.getName());
        wallet.setWalletTypeId(walletDto.getWalletTypeId());
        return wallet;
    }

    private String setUniqueIban(){
        String iban = generateRandomIBAN();
        try {
            walletService.checkIbanExistence(iban);
            return setUniqueIban();
        }
        catch (EntityNotFoundException e){
            return iban;
        }
    }

    public WalletResponseDto toDto(Wallet wallet, List<User> walletUsers) {
        WalletResponseDto dto = new WalletResponseDto();
        dto.setWalletId(wallet.getWalletId());
        dto.setBalance(wallet.getBalance());
        dto.setIban(wallet.getIban());
        dto.setName(wallet.getName());
        dto.setType(WALLET_TYPE_ID_1 == wallet.getWalletTypeId() ? WALLET_TYPE_1
                : (WALLET_TYPE_ID_2 == wallet.getWalletTypeId() ? WALLET_TYPE_2 : EMPTY));
        dto.setWalletUsers(walletUsers);
        return dto;
    }
}
