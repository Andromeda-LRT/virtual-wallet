package com.virtualwallet.repositories;

import com.virtualwallet.models.WalletType;
import com.virtualwallet.repositories.contracts.WalletTypeRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WalletTypeRepositoryImpl extends AbstractCrudRepository<WalletType> implements WalletTypeRepository {
    @Autowired
    public WalletTypeRepositoryImpl(SessionFactory sessionFactory) {
        super(WalletType.class, sessionFactory);
    }
}
