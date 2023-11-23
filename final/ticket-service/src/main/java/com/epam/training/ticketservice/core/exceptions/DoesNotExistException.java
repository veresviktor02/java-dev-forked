package com.epam.training.ticketservice.core.exceptions;

public class DoesNotExistException extends Exception {

    public DoesNotExistException(String message) {
        super(message);
    }
}
