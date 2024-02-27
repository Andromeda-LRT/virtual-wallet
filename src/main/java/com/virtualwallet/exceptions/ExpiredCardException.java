package com.virtualwallet.exceptions;

public class ExpiredCardException extends RuntimeException{
    public ExpiredCardException(String message) {
        super(message);
    }
}
