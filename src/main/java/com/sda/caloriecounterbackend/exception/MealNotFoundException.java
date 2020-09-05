package com.sda.caloriecounterbackend.exception;

public class MealNotFoundException extends RuntimeException {
    public MealNotFoundException() {
    }

    public MealNotFoundException(String message) {
        super(message);
    }
}
