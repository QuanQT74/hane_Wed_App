package com.henawedapp.backend.exception;

import com.henawedapp.backend.dto.response.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler cho REST API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ValidationErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ValidationErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Dữ liệu đầu vào không hợp lệ")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationErrorResponse> handleBindException(
            BindException ex,
            HttpServletRequest request) {

        List<ValidationErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ValidationErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Binding Failed")
                .message("Dữ liệu đầu vào không hợp lệ")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ValidationErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Duplicate Resource")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ValidationErrorResponse> handleInvalidOtpException(
            InvalidOtpException ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid OTP")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ValidationErrorResponse> handleOtpExpiredException(
            OtpExpiredException ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.GONE.value())
                .error("OTP Expired")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.GONE).body(response);
    }

    @ExceptionHandler(MaxOtpAttemptsExceededException.class)
    public ResponseEntity<ValidationErrorResponse> handleMaxOtpAttemptsExceededException(
            MaxOtpAttemptsExceededException ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Max OTP Attempts Exceeded")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    @ExceptionHandler(PendingRegistrationNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handlePendingRegistrationNotFoundException(
            PendingRegistrationNotFoundException ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Pending Registration Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RegistrationExpiredException.class)
    public ResponseEntity<ValidationErrorResponse> handleRegistrationExpiredException(
            RegistrationExpiredException ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.GONE.value())
                .error("Registration Expired")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.GONE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Đã xảy ra lỗi không mong muốn: " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        // Log the full exception for debugging
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
