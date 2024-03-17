package com.virtualwallet.utils;

import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.models.User;
import com.virtualwallet.models.mvc_input_model_dto.TransactionModelFilterDto;

import static com.virtualwallet.model_helpers.ModelConstantHelper.UNAUTHORIZED_OPERATION_ERROR_MESSAGE;

public class UtilHelpers {
    public static WalletTransactionModelFilterOptions populateWalletTransactionFilterOptions
            (TransactionModelFilterDto dto) {
        return new WalletTransactionModelFilterOptions(
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getSender(),
                dto.getRecipient(),
                dto.getDirection(),
                dto.getSortBy(),
                dto.getSortOrder()
        );
    }
    public static CardTransactionModelFilterOptions populateCardTransactionFilterOptions
            (TransactionModelFilterDto dto) {
        return new CardTransactionModelFilterOptions(
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getSender(),
                dto.getRecipient(),
                dto.getDirection(),
                dto.getSortBy(),
                dto.getSortOrder()
        );
    }
}
