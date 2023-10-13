package com.example.cloudassignment03.exceptions;

public class CannotAccessException extends RuntimeException{
    public CannotAccessException(String message) {
        super(message);
    }
}
