package com.virtualwallet.model_mappers;

import com.virtualwallet.models.Card;
import com.virtualwallet.models.model_dto.CardDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;

@Component
public class CardMapper {
    public Card fromDto(CardDto cardDto) {
        Card card = new Card();
        card.setNumber(cardDto.getNumber());
        card.setExpirationDate(convertToLocalDateTime(cardDto.getExpirationMonth(), cardDto.getExpirationYear()));
        card.setCardHolder(cardDto.getCardHolder());
        card.setCheckNumber(cardDto.getCheckNumber());
        card.setCardType(cardDto.getCardType());
        card.setArchived(false);
        return card;
    }

    public Card fromDto(CardDto cardDto, int id) {
        Card card = new Card();
        card.setId(id);
        card.setNumber(cardDto.getNumber());
        card.setExpirationDate(convertToLocalDateTime(cardDto.getExpirationMonth(), cardDto.getExpirationYear()));
        card.setCardHolder(cardDto.getCardHolder());
        card.setCheckNumber(cardDto.getCheckNumber());
        card.setCardType(cardDto.getCardType());
        return card;
    }

    public CardDto toDto(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.setNumber(card.getNumber());
        cardDto.setExpirationMonth(card.getExpirationDate().getMonth());
        cardDto.setExpirationYear(Year.of(card.getExpirationDate().getYear()));
        cardDto.setCardHolder(card.getCardHolder());
        cardDto.setCheckNumber(card.getCheckNumber());
        cardDto.setCardType(card.getCardType());
        return cardDto;
    }

    private LocalDateTime convertToLocalDateTime(Month month, Year year) {
        LocalDateTime date = LocalDateTime.of(year.getValue(), month.getValue(), month.maxLength(),
                23, 59, 59, 999999999);
        return date;
    }
}
