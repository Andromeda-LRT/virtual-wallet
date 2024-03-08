package com.virtualwallet.model_mappers;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.model_dto.WalletDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.virtualwallet.services.contracts.WalletService;

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
        return wallet;
    }

    public Wallet fromDto(WalletDto walletDto, int id, User user){
        Wallet wallet = walletService.getWalletById(user, id);
        wallet.setName(walletDto.getName());
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


}
