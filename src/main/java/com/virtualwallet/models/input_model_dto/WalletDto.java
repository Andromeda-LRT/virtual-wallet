package com.virtualwallet.models.input_model_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.virtualwallet.model_helpers.ModelConstantHelper.EMPTY_ERROR_MESSAGE;
import static com.virtualwallet.model_helpers.ModelConstantHelper.NAME_ERROR_MESSAGE;

public class WalletDto {
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 3, max = 20, message = NAME_ERROR_MESSAGE)
    String name;

    @NotNull(message = EMPTY_ERROR_MESSAGE)
    int walletTypeId;

    public WalletDto() {
    }

    public WalletDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWalletTypeId() {
        return walletTypeId;
    }

    public void setWalletTypeId(int walletTypeId) {
        this.walletTypeId = walletTypeId;
    }
}
