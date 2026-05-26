package com.personalfinance.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;

public record TransactionUpdateRequest(
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        String category,

        String description
) {
}
