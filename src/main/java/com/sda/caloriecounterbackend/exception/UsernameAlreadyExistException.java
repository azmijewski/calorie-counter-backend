package com.sda.caloriecounterbackend.exception;

public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException() {
    }

    public UsernameAlreadyExistException(String message) {
        super(message);
    }
}
