package com.personalfinance.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(String message, List<String> errors, Instant timestamp) {

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, List.of(), Instant.now());
    }

    public static ErrorResponse of(String message, List<String> errors) {
        return new ErrorResponse(message, errors, Instant.now());
    }
}
