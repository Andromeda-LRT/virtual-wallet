package com.virtualwallet.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String type, String attribute, String value){
        super(String.format("%s, with %s %s not found.", type, attribute, value));
    }

    public EntityNotFoundException(String type, String attribute, String value1, String value2){
        super(String.format("%s, with %s %s does not have %s.", type, attribute, value1, value2));
    }

}
