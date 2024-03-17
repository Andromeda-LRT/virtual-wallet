package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.CardType;
import com.virtualwallet.models.User;
import com.virtualwallet.models.WalletType;
import com.virtualwallet.repositories.contracts.CardTypeRepository;
import com.virtualwallet.repositories.contracts.WalletTypeRepository;
import com.virtualwallet.services.contracts.CardTypeService;
import com.virtualwallet.services.contracts.WalletTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.UNAUTHORIZED_OPERATION_ERROR_MESSAGE;

@Service
public class WalletTypeServiceImpl implements WalletTypeService {
    private final WalletTypeRepository walletTypeRepository;

    @Autowired
    public WalletTypeServiceImpl(WalletTypeRepository walletTypeRepository) {
        this.walletTypeRepository = walletTypeRepository;
    }

    @Override
    public WalletType createWalletType(User user, WalletType walletType) {
        checkIfAdmin(user);
        walletTypeRepository.create(walletType);
        return walletType;
    }

    @Override
    public void deleteWalletType(int walletTypeId, User user) {
        checkIfAdmin(user);
        walletTypeRepository.delete(walletTypeId);
    }

    @Override
    public void updateWalletType(WalletType walletType, User user) {
        checkIfAdmin(user);
        boolean duplicateStatusNameExists = true;
        try {
            walletTypeRepository.getByStringField("name", walletType.getType());
        } catch (EntityNotFoundException e) {
            duplicateStatusNameExists = false;
        }
        if (duplicateStatusNameExists) {
            throw new DuplicateEntityException("CardType", "name", walletType.getType());
        }
        walletTypeRepository.create(walletType);
    }

    @Override
    public WalletType getWalletType(int walletTypeId) {
        return walletTypeRepository.getById(walletTypeId);
    }

    @Override
    public List<WalletType> getAllWalletTypes() {
        return walletTypeRepository.getAll();
    }

    private static void checkIfAdmin(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }
}
