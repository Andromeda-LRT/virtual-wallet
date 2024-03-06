package com.virtualwallet.services.contracts;

import com.virtualwallet.models.CheckNumber;
import com.virtualwallet.models.User;

import java.util.List;

public interface CheckNumberService {
    CheckNumber createCheckNumber(String checkNumber);
    void deleteCheckNumber(int checkNumberId, User user);
    void updateCheckNumber(CheckNumber checkNumber, User user);
    CheckNumber getCheckNumberById(int checkNumberId);
    CheckNumber getCheckNumberByNumber(String cvvNumber);
    List<CheckNumber> getAllCheckNumbers();
}
