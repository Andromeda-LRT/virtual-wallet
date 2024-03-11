package com.virtualwallet.model_helpers;

import java.time.LocalDateTime;
import java.util.Optional;

public class CardTransactionModelFilterOptions {

    private Optional<LocalDateTime> startDate;
    private Optional<LocalDateTime> endDate;
    private Optional<String> cardLastFourDigits;
    private Optional<String> recipient;
    private Optional<String> direction;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public CardTransactionModelFilterOptions(LocalDateTime startDate,
                                             LocalDateTime  endDate,
                                             String cardLastFourDigits,
                                             String recipient,
                                             String direction,
                                             String sortBy,
                                             String sortOrder) {
        this.startDate = Optional.ofNullable(startDate);
        this.endDate = Optional.ofNullable(endDate);
        this.cardLastFourDigits = Optional.ofNullable(cardLastFourDigits);
        this.recipient = Optional.ofNullable(recipient);
        this.direction = Optional.ofNullable(direction);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }


    public Optional<LocalDateTime> getStartDate() {
        return startDate;
    }

    public void setStartDate(Optional<LocalDateTime> startDate) {
        this.startDate = startDate;
    }

    public Optional<LocalDateTime> getEndDate() {
        return endDate;
    }

    public void setEndDate(Optional<LocalDateTime> endDate) {
        this.endDate = endDate;
    }

    public Optional<String> getSender() {
        return cardLastFourDigits;
    }

    public void setSender(Optional<String> sender) {
        this.cardLastFourDigits = sender;
    }

    public Optional<String> getRecipient() {
        return recipient;
    }

    public void setRecipient(Optional<String> recipient) {
        this.recipient = recipient;
    }

    public Optional<String> getDirection() {
        return direction;
    }

    public void setDirection(Optional<String> direction) {
        this.direction = direction;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(Optional<String> sortBy) {
        this.sortBy = sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Optional<String> sortOrder) {
        this.sortOrder = sortOrder;
    }
}

