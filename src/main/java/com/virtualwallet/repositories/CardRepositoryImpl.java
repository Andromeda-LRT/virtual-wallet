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
    public void addCardToUser(int userId, int cardId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String sql = ("INSERT INTO users_cards (card_id, user_id) " +
                    "VALUES (:cardId, :userId)");

            session.createNativeQuery(sql)
                    .setParameter("cardId", cardId)
                    .setParameter("userId", userId)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }
    //todo test out once we have values inside DB.

    @Override
    public void removeCardFromUser(int userId, int cardId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String sql = ("DELETE FROM users_cards where user_id = :userId and card_id = :cardId");

            session.createNativeQuery(sql)
                    .setParameter("cardId", cardId)
                    .setParameter("userId", userId)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }

    //todo test out once we have values inside DB.
    @Override
    public Card getUserCard(User user, int cardId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String sql = ("select * from cards c " +
                    "join users_cards uc on c.card_id = uc.card_id" +
                    " where user_id =:userId and c.card_id =:cardId");

            Query<Card> query = session.createNativeQuery(sql, Card.class);

            List<Card> result = query.getResultList();
            if (result == null) {
                throw new EntityNotFoundException("user", "id", String.valueOf(user.getId()), "cards");
            }
            return result.get(0);
        }
    }
}
