package com.virtualwallet.utils;

import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.User;

import static com.virtualwallet.model_helpers.ModelConstantHelper.UNAUTHORIZED_OPERATION_ERROR_MESSAGE;

public class UtilHelpers {
    public void verifyUserAccess(int user_id, User user) {
        if (user.getId() != user_id){
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }
}
