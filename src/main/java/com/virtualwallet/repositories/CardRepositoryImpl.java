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

    //todo test out once we have values inside DB.

    @Override
    public List<Card> getAllUserCards(int userid) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String sql = ("SELECT c.card_id, c.number, c.expiration_date, c.card_holder, c.card_type_id, " +
                    "c.check_number_id, c.is_archived " +
                    "FROM users u " +
                    "JOIN users_cards uc ON u.user_id = uc.user_id " +
                    "JOIN cards c ON c.card_id = uc.card_id " +
                    "WHERE u.user_id =:userId");

            Query<Card> query = session.createNativeQuery(sql, Card.class)
                    .setParameter("userId", userid);

            List<Card> result = query.getResultList();
            if (result == null) {
                throw new EntityNotFoundException("user", "id", String.valueOf(userid), "cards");
            }
            return result;
        }
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
            String sql = ("DELETE FROM users_cards WHERE user_id = :userId AND card_id = :cardId");

            session.createNativeQuery(sql)
                    .setParameter("cardId", cardId)
                    .setParameter("userId", userId)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }

    //todo test out once we have values inside DB.

    /**
     *
     * @param user
     * @param cardId
     * @return
     */
    @Override
    public Card getUserCard(User user, int cardId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String sql = ("SELECT c.card_id, c.number, c.expiration_date, c.card_type_id, " +
                    "c.card_holder, c.check_number_id, c.is_archived FROM cards c " +
                    "JOIN users_cards uc ON c.card_id = uc.card_id " +
                    "WHERE user_id =:userId AND c.card_id =:cardId");

            Query<Card> query = session.createNativeQuery(sql, Card.class)
                    .setParameter("userId", user.getId())
                    .setParameter("cardId", cardId);

            List<Card> result = query.getResultList();
            if (result == null) {
                throw new EntityNotFoundException("user", "id", String.valueOf(user.getId()), "cards");
            }
            return result.get(0);
        }
    }

}
