package com.virtualwallet.repositories;

import com.virtualwallet.models.CardType;
import com.virtualwallet.repositories.contracts.CardTypeRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CardTypeRepositoryImpl extends AbstractCrudRepository<CardType> implements CardTypeRepository {
    @Autowired
    public CardTypeRepositoryImpl(SessionFactory sessionFactory) {
        super(CardType.class, sessionFactory);
    }
}
