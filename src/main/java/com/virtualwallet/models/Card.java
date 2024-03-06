package com.virtualwallet.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private int id;
    @Column(name = "number")
    private String number;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "card_holder")
    private String cardHolder;
    @ManyToOne
    @JoinColumn(name = "cvv_number_id")
    private CheckNumber checkNumber;

    @ManyToOne
    @JoinColumn(name = "card_type_id")
    private CardType cardType;

    @Column(name = "is_archived")
    private boolean isArchived;


    public Card() {
    }

    public Card(int id, String number,
                LocalDateTime expirationDate,
                String cardHolder,
                CheckNumber checkNumber,
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

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public CheckNumber getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(CheckNumber checkNumber) {
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
