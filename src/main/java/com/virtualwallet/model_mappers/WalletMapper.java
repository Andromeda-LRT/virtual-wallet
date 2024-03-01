package com.virtualwallet.model_mappers;

import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.model_dto.WalletDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.virtualwallet.services.contracts.WalletService;
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
        return wallet;
        //TODO add an Iban creation
    }

    public Wallet fromDto(WalletDto walletDto, int id){
        Wallet wallet = new Wallet();
        wallet.setWalletId(id);
        wallet.setName(walletDto.getName());
        return wallet;
    }


}
