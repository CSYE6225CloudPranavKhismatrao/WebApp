package com.example.cloudassignment03.exceptions;

public class CannotSubmitException extends RuntimeException{
    public CannotSubmitException(String message) {
        super(message);
    }
}
