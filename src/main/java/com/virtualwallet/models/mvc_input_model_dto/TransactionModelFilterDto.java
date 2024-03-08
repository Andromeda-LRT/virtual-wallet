package com.virtualwallet.models.mvc_input_model_dto;

import java.time.LocalDateTime;

public class TransactionModelFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sender;
    private String recipient;
    private String direction;
    private String sortBy;
    private String sortOrder;

    public TransactionModelFilterDto() {
    }

    public TransactionModelFilterDto(LocalDateTime startDate,
                                     LocalDateTime endDate,
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
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
