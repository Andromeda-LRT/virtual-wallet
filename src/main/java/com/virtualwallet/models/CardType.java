package com.virtualwallet.models;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "card_types")
public class CardType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_type_id")
    int id;
    @Column(name = "type")
    String type;

    public CardType() {
    }

    public CardType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String roleName) {
        this.type = roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardType cardType)) return false;
        return getId() == cardType.getId() && Objects.equals(getType(), cardType.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType());
    }
}
