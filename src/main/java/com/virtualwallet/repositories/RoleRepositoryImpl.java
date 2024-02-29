package com.virtualwallet.repositories;

import com.virtualwallet.models.Role;
import com.virtualwallet.repositories.contracts.RoleRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl extends AbstractCrudRepository<Role> implements RoleRepository {
    @Autowired
    public RoleRepositoryImpl(Class<Role> klas, SessionFactory sessionFactory) {
        super(klas, sessionFactory);
    }
}
