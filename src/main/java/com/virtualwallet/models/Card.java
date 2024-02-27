package com.virtualwallet.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;
    @Column(name = "number")
    private String number;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    @ManyToOne
    @Column(name = "card_holder_id")
    private User cardHolder;
    @Column(name = "check_number")
    private int checkNumber;

    @ManyToOne
    @Column(name = "card_type_id")
    private CardType cardType;

    @Column(name = "is_archived")
    private boolean isArchived;


    public Card() {
    }

    public Card(int id, String number,
                LocalDateTime expirationDate,
                User cardHolder,
                int checkNumber,
                CardType cardType) {
        this.id = id;
        this.number = number;
        this.expirationDate = expirationDate;
        this.cardHolder = cardHolder;
        this.checkNumber = checkNumber;
        this.cardType = cardType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(User cardHolder) {
        this.cardHolder = cardHolder;
    }

    public int getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(int checkNumber) {
        this.checkNumber = checkNumber;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
