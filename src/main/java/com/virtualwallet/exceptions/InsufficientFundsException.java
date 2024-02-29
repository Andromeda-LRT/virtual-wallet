package com.virtualwallet.exceptions;

import static com.virtualwallet.model_helpers.ModelConstantHelper.INSUFFICIENT_FUNDS_ERROR_MESSAGE;

public class InsufficientFundsException extends RuntimeException{
    /**
     * Displays required amount necessary to complete the transaction.
     * <br/>
     * Example: InsufficientFundsException("wallet", wallet.getIban, 200, 500)
     * will display the following message: <br/>wallet with iban BG18RZBB91550123456789
     * requires 300 more funds to complete the transaction.
     * @param walletModel just the String "wallet"
     * @param walletIban the iban of the current wallet.
     * @param currentAmount current balance in wallet.
     * @param desiredAmount amount desired to be transferred.
     */

    public InsufficientFundsException(String walletModel, String walletIban,
                                      double currentAmount, double desiredAmount){
        super(String.format(INSUFFICIENT_FUNDS_ERROR_MESSAGE,
                walletModel, walletIban, (desiredAmount - currentAmount)));
    }
}
