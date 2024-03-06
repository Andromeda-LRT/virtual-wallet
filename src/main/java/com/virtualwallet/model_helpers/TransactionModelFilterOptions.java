package com.virtualwallet.model_helpers;

import java.sql.Time;
import java.util.Optional;

public class TransactionModelFilterOptions {

    private Optional<Time> startDate;
    private Optional<Time> endDate;
    private Optional<String> sender;
    private Optional<String> recipient;
    private Optional<String> direction;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public TransactionModelFilterOptions(Time startDate,
                                         Time endDate,
                                         String sender,
                                         String recipient,
                                         String direction,
                                         String sortBy,
                                         String sortOrder) {
        this.startDate = Optional.ofNullable(startDate);
        this.endDate = Optional.ofNullable(endDate);
        this.sender = Optional.ofNullable(sender);
        this.recipient = Optional.ofNullable(recipient);
        this.direction = Optional.ofNullable(direction);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }


    public Optional<Time> getStartDate() {
        return startDate;
    }

    public void setStartDate(Optional<Time> startDate) {
        this.startDate = startDate;
    }

    public Optional<Time> getEndDate() {
        return endDate;
    }

    public void setEndDate(Optional<Time> endDate) {
        this.endDate = endDate;
    }

    public Optional<String> getSender() {
        return sender;
    }

    public void setSender(Optional<String> sender) {
        this.sender = sender;
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

