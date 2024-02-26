package com.virtualwallet.exceptions;

public class AuthenticationFailureException extends RuntimeException {
    public AuthenticationFailureException(String message) {
        super(message);

    }
}
