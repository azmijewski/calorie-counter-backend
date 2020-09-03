package com.sda.caloriecounterbackend.exception;

public class UserProductNotFoundException extends RuntimeException {
    public UserProductNotFoundException() {
    }

    public UserProductNotFoundException(String message) {
        super(message);
    }
}
