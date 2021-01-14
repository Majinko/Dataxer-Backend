package com.data.dataxer.exceptions;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super("Invalid format. " + message);
    }

}
