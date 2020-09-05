package com.sda.caloriecounterbackend.exception;

public class MealProductNotFoundException extends RuntimeException {
    public MealProductNotFoundException() {
    }

    public MealProductNotFoundException(String message) {
        super(message);
    }
}
