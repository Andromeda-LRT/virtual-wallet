package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.CardType;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardTypeRepository;
import com.virtualwallet.services.contracts.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.UNAUTHORIZED_OPERATION_ERROR_MESSAGE;

@Service
public class CardTypeServiceImpl implements CardTypeService {
    private final CardTypeRepository cardTypeRepository;

    @Autowired
    public CardTypeServiceImpl(CardTypeRepository cardTypeRepository) {
        this.cardTypeRepository = cardTypeRepository;
    }

    @Override
    public CardType createCardType(User user, CardType cardType) {
        checkIfAdmin(user);
        cardTypeRepository.create(cardType);
        return cardType;
    }

    @Override
    public void deleteCardType(int cardTypeId, User user) {
        checkIfAdmin(user);
        cardTypeRepository.delete(cardTypeId);
    }

    @Override
    public void updateCardType(CardType cardType, User user) {
        checkIfAdmin(user);
        boolean duplicateStatusNameExists = true;
        try {
            cardTypeRepository.getByStringField("name", cardType.getType());
        } catch (EntityNotFoundException e) {
            duplicateStatusNameExists = false;
        }
        if (duplicateStatusNameExists) {
            throw new DuplicateEntityException("CardType", "name", cardType.getType());
        }
        cardTypeRepository.create(cardType);
    }

    @Override
    public CardType getCardType(int cardTypeId) {
        return cardTypeRepository.getById(cardTypeId);
    }

    @Override
    public List<CardType> getAllCardTypes() {
        return cardTypeRepository.getAll();
    }

    private static void checkIfAdmin(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }
}
