package com.example.loyalty.exceptions;
/**
 * ValidationException is a custom runtime exception used to indicate
 * that input validation has failed.
 *
 * Typically thrown when a request DTO does not meet required criteria,
 * e.g., missing mandatory fields, invalid formats, or constraint violations.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
