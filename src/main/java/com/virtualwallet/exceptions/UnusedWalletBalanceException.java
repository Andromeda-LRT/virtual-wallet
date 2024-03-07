package com.virtualwallet.exceptions;

import static java.lang.String.format;

public class UnusedWalletBalanceException extends RuntimeException {
    public UnusedWalletBalanceException(String value){
        super(format("Wallet has %s available balance", value));
    }

    public UnusedWalletBalanceException(String walletIban, String value){
        super(format("Wallet with %s has %s available balance", walletIban, value));
    }
}
