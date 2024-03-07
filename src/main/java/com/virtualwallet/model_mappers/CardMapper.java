package com.virtualwallet.model_mappers;

import com.virtualwallet.models.Card;
import com.virtualwallet.models.CardType;
import com.virtualwallet.models.model_dto.CardDto;
import com.virtualwallet.models.model_dto.CardForAddingMoneyToWalletDto;
import com.virtualwallet.services.contracts.CardTypeService;
import com.virtualwallet.services.contracts.CheckNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;

@Component
public class CardMapper {
    private final CardTypeService cardTypeService;
    private final CheckNumberService checkNumberService;

@Autowired
    public CardMapper(CardTypeService cardTypeService, CheckNumberService checkNumberService) {
        this.cardTypeService = cardTypeService;
    this.checkNumberService = checkNumberService;
}

    public Card fromDto(CardDto cardDto) {
        Card card = new Card();
        card.setNumber(cardDto.getNumber());
        card.setExpirationDate(convertToLocalDateTime(cardDto.getExpirationMonth(), cardDto.getExpirationYear()));
        card.setCardHolder(cardDto.getCardHolder());
        card.setCheckNumber(checkNumberService.createCheckNumber(cardDto.getCheckNumber()));
        card.setCardType(cardTypeService.getCardType(cardDto.getCardType()));
        card.setArchived(false);
        return card;
    }

    public Card fromDto(CardDto cardDto, int id) {
        Card card = new Card();
        card.setId(id);
        card.setNumber(cardDto.getNumber());
        card.setExpirationDate(convertToLocalDateTime(cardDto.getExpirationMonth(), cardDto.getExpirationYear()));
        card.setCardHolder(cardDto.getCardHolder());
        card.setCheckNumber(checkNumberService.createCheckNumber(cardDto.getCheckNumber()));
        card.setCardType(cardTypeService.getCardType(cardDto.getCardType()));
        card.setArchived(false);
        return card;
    }

    public CardDto toDto(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.setNumber(card.getNumber());
        cardDto.setExpirationMonth(card.getExpirationDate().getMonth());
        cardDto.setExpirationYear(Year.of(card.getExpirationDate().getYear()));
        cardDto.setCardHolder(card.getCardHolder());
        cardDto.setCheckNumber(card.getCheckNumber().getCvv());
        cardDto.setCardType(card.getCardType().getId());
        return cardDto;
    }

    public CardForAddingMoneyToWalletDto toDummyApiDto(Card card) {
        CardForAddingMoneyToWalletDto cardDto = new CardForAddingMoneyToWalletDto();
        cardDto.setNumber(card.getNumber());
        cardDto.setExpirationDate(card.getExpirationDate());
        cardDto.setCardHolder(cardDto.getCardHolder());
        cardDto.setCheckNumber(card.getCheckNumber().getCvv());
        cardDto.setCardType(card.getCardType().getType());
        return cardDto;
    }

    private LocalDateTime convertToLocalDateTime(Month month, Year year) {
        LocalDateTime date = LocalDateTime.of(year.getValue(), month.getValue(), month.maxLength(),
                23, 59, 59);
        return date;
    }
}
