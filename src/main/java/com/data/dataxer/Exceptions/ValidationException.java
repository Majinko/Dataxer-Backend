package com.data.dataxer.Exceptions;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super("Invalid format. " + message);
    }

}
