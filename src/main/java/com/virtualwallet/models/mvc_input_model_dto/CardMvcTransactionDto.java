package com.virtualwallet.models.mvc_input_model_dto;

import com.virtualwallet.models.input_model_dto.CardTransactionDto;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class CardMvcTransactionDto extends CardTransactionDto {
    @NotNull(message = "cardId cannot be empty")
    private int cardId;

    public CardMvcTransactionDto() {
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardMvcTransactionDto that)) return false;
        return cardId == that.cardId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }
}
