package com.virtualwallet.repositories;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.models.CheckNumber;
import com.virtualwallet.repositories.contracts.CheckNumberRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CheckNumberRepositoryImpl extends AbstractCrudRepository<CheckNumber> implements CheckNumberRepository {
    @Autowired
    public CheckNumberRepositoryImpl(SessionFactory sessionFactory) {
        super(CheckNumber.class, sessionFactory);
    }

    @Override
    public CheckNumber getByNumber(int cvv) {
        try (Session session = sessionFactory.openSession()) {
            Query<CheckNumber> query = session.createQuery
                    ("From CheckNumber where cvvNumber =:cvv", CheckNumber.class)
                    .setParameter("cvv", cvv);

            List<CheckNumber> result = query.list();
            if (result == null) {
                throw new EntityNotFoundException("CheckNumber", "number", String.valueOf(cvv));
            }
            return result.get(0);
        }
    }
}
