package com.henawedapp.backend.exception;

/**
 * Exception khi file upload không hợp lệ.
 */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}
