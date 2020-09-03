package com.sda.caloriecounterbackend.exception;

public class PhotoNotFoundException extends RuntimeException {
    public PhotoNotFoundException() {
    }

    public PhotoNotFoundException(String message) {
        super(message);
    }
}
