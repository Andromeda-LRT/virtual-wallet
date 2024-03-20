package com.virtualwallet.repositories;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.CardRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class CardRepositoryImpl extends AbstractCrudRepository<Card> implements CardRepository {

    @Autowired
    public CardRepositoryImpl(SessionFactory sessionFactory) {
        super(Card.class, sessionFactory);
    }

    @Override
    public Card getUserCard(User user, int cardId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String sql = ("SELECT * FROM cards c WHERE c.card_holder_id = :userId AND c.card_id = :cardId");

            Query<Card> query = session.createNativeQuery(sql, Card.class)
                    .setParameter("userId", user.getId())
                    .setParameter("cardId", cardId);

            List<Card> result = query.getResultList();
            session.getTransaction().commit();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Card", "id" , String.valueOf(cardId));
            }
            return result.get(0);
        }
    }

}
