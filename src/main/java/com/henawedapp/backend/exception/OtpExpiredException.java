package com.henawedapp.backend.exception;

/**
 * Exception thrown when OTP has expired.
 */
public class OtpExpiredException extends RuntimeException {

    public OtpExpiredException(String message) {
        super(message);
    }
}
