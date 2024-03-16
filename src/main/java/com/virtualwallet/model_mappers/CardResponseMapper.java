package com.virtualwallet.model_mappers;

import com.virtualwallet.models.Card;
import com.virtualwallet.models.response_model_dto.CardResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.CARD_LAST_4_DIGIT_START_INDEX;
import static com.virtualwallet.model_helpers.ModelConstantHelper.HIDDEN_CARD_DIGITS;

@Component
public class CardResponseMapper {



    public CardResponseMapper() {
    }

    public CardResponseDto toResponseDto(Card card) {
        CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setId(card.getId());
        cardResponseDto.setNumber(HIDDEN_CARD_DIGITS + card.getNumber().substring(CARD_LAST_4_DIGIT_START_INDEX));
        cardResponseDto.setCardHolder(card.getCardHolder());
        cardResponseDto.setExpirationDate(card.getExpirationDate());
        cardResponseDto.setCheckNumber(card.getCheckNumber().getCvv());
        cardResponseDto.setCardType(card.getCardType().getType());
        return cardResponseDto;
    }

    public List<CardResponseDto> toResponseDtoList(List<Card> cards) {
        List<CardResponseDto> cardResponseDtoList = new ArrayList<>();
        for (Card c : cards) {
            cardResponseDtoList.add(toResponseDto(c));
        }
        return cardResponseDtoList;
    }
}
