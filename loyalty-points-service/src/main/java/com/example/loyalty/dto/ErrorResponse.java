package com.example.loyalty.dto;

/**
 * ErrorResponse represents a standard error message returned by the API.
 *
 * This DTO is used for sending JSON error responses with an "error" field.
 */
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
