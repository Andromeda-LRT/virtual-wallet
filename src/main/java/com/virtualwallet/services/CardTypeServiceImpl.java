package com.virtualwallet.services;

import com.virtualwallet.models.CardType;
import com.virtualwallet.repositories.contracts.CardTypeRepository;
import com.virtualwallet.services.contracts.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardTypeServiceImpl implements CardTypeService {
    //TODO implement CardTypeService - create, update, delete, getAll
    private final CardTypeRepository cardTypeRepository;
    @Autowired
    public CardTypeServiceImpl(CardTypeRepository cardTypeRepository) {
        this.cardTypeRepository = cardTypeRepository;
    }

    @Override
    public void create(CardType entity) {

    }

    @Override
    public void update(CardType entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<CardType> getAll() {
        return null;
    }

    @Override
    public CardType getById(int id) {
        return cardTypeRepository.getById(id);
    }
}
