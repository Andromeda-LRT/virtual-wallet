package com.virtualwallet.repositories;

import com.virtualwallet.models.Status;
import com.virtualwallet.repositories.contracts.StatusRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StatusRepositoryImpl extends AbstractCrudRepository<Status> implements StatusRepository {
    @Autowired
    public StatusRepositoryImpl(SessionFactory sessionFactory) {
        super(Status.class, sessionFactory);
    }
}
