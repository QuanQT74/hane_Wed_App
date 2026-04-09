package com.henawedapp.backend.exception;

/**
 * Exception thrown when max OTP attempts exceeded.
 */
public class MaxOtpAttemptsExceededException extends RuntimeException {

    public MaxOtpAttemptsExceededException(String message) {
        super(message);
    }
}
