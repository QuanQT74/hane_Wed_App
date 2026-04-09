package com.henawedapp.backend.exception;

/**
 * Exception thrown when OTP is invalid.
 */
public class InvalidOtpException extends RuntimeException {

    public InvalidOtpException(String message) {
        super(message);
    }
}
