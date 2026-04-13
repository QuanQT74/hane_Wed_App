package com.henawedapp.backend.exception;

/**
 * Exception khi member type không hợp lệ cho operation.
 */
public class InvalidMemberTypeException extends RuntimeException {

    public InvalidMemberTypeException(String message) {
        super(message);
    }

    public InvalidMemberTypeException() {
        super("Loại hội viên không hợp lệ cho thao tác này.");
    }
}
