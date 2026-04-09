package com.henawedapp.backend.exception;

/**
 * Exception thrown when registration is expired.
 */
public class RegistrationExpiredException extends RuntimeException {

    public RegistrationExpiredException(String message) {
        super(message);
    }
}
