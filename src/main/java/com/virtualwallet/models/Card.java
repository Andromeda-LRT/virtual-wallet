package com.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Column(name = "card_holder_full_name")
    private String cardHolder;
    @ManyToOne
    @JoinColumn(name = "cvv_number_id")
    private CheckNumber checkNumber;

    @ManyToOne
    @JoinColumn(name = "card_type_id")
    private CardType cardType;

    @Column(name = "is_archived")
    private boolean isArchived;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "card_holder_id")
    private User cardHolderId;

    public Card() {
    }

    public Card(int id, String number,
                LocalDateTime expirationDate,
                String cardHolder,
                CheckNumber checkNumber,
                CardType cardType, User cardHolderId) {
        this.id = id;
        this.number = number;
        this.expirationDate = expirationDate;
        this.cardHolder = cardHolder;
        this.checkNumber = checkNumber;
        this.cardType = cardType;
        this.cardHolderId = cardHolderId;
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
        this.isArchived = archived;
    }

    public User getCardHolderId() {
        return cardHolderId;
    }

    public void setCardHolderId(User cardHolderId) {
        this.cardHolderId = cardHolderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return getId() == card.getId()
                && isArchived() == card.isArchived()
                && Objects.equals(getNumber(), card.getNumber())
                && Objects.equals(getExpirationDate(), card.getExpirationDate())
                && Objects.equals(getCardHolder(), card.getCardHolder())
                && Objects.equals(getCheckNumber(), card.getCheckNumber())
                && Objects.equals(getCardType(), card.getCardType())
                && Objects.equals(getCardHolderId(), card.getCardHolderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getNumber(),
                getExpirationDate(),
                getCardHolder(),
                getCheckNumber(),
                getCardType(),
                isArchived(),
                getCardHolderId());
    }
}
