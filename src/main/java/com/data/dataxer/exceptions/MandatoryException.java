package com.data.dataxer.exceptions;

public class MandatoryException extends RuntimeException {

    public MandatoryException(String field) {
        super("Mandatory field " + field + " is missing");
    }

}
