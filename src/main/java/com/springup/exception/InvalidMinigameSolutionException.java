package com.springup.exception;

public class InvalidMinigameSolutionException extends RuntimeException {
    public InvalidMinigameSolutionException(String message) {
        super(message);
    }
}