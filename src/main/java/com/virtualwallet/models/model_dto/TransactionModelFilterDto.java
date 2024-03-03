package com.virtualwallet.models.model_dto;

import java.sql.Time;

public class TransactionModelFilterDto {
    private Time startDate;
    private Time endDate;
    private String sender;
    private String recipient;
    private String direction;
    private String sortBy;
    private String sortOrder;

    public TransactionModelFilterDto() {
    }

    public TransactionModelFilterDto(Time startDate,
                                     Time endDate,
                                     String sender,
                                     String recipient,
                                     String direction,
                                     String sortBy,
                                     String sortOrder) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.sender = sender;
        this.recipient = recipient;
        this.direction = direction;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public Time getStartDate() {
        return startDate;
    }

    public void setStartDate(Time startDate) {
        this.startDate = startDate;
    }

    public Time getEndDate() {
        return endDate;
    }

    public void setEndDate(Time endDate) {
        this.endDate = endDate;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
