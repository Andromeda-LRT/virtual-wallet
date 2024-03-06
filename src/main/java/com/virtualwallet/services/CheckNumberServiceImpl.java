package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.CheckNumber;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CheckNumberRepository;
import com.virtualwallet.services.contracts.CheckNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.UNAUTHORIZED_OPERATION_ERROR_MESSAGE;

@Service
public class CheckNumberServiceImpl implements CheckNumberService {
    private final CheckNumberRepository checkNumberRepository;

    @Autowired
    public CheckNumberServiceImpl(CheckNumberRepository checkNumberRepository) {
        this.checkNumberRepository = checkNumberRepository;
    }

    @Override
    public CheckNumber createCheckNumber(String checkNumber) {
//        checkIfAdmin(user);
        try{
            return checkNumberRepository.getByNumber(checkNumber);
        } catch (EntityNotFoundException e) {
            checkNumberRepository.create(new CheckNumber(checkNumber));
            return checkNumberRepository.getByNumber(checkNumber);
        }
    }

    @Override
    public void deleteCheckNumber(int checkNumberId, User user) {
        checkIfAdmin(user);
        checkNumberRepository.delete(checkNumberId);
    }

    @Override
    public void updateCheckNumber(CheckNumber checkNumber, User user) {
        checkIfAdmin(user);
        boolean duplicateStatusNameExists = true;
        try {
            checkNumberRepository.getByNumber(checkNumber.getCvv());
        } catch (EntityNotFoundException e) {
            duplicateStatusNameExists = false;
        }
        if (duplicateStatusNameExists) {
            throw new DuplicateEntityException("CheckNumber", "number", String.valueOf(checkNumber.getCvv()));
        }
        checkNumberRepository.update(checkNumber);
    }

    @Override
    public CheckNumber getCheckNumberById(int checkNumberId) {
        return checkNumberRepository.getById(checkNumberId);
    }

    @Override
    public List<CheckNumber> getAllCheckNumbers() {
        return checkNumberRepository.getAll();
    }

    @Override
    public CheckNumber getCheckNumberByNumber(String cvvNumber) {
        return checkNumberRepository.getByNumber(cvvNumber);
    }

    private static void checkIfAdmin(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }
}
