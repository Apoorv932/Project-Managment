package com.personalfinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

public record GoalUpdateRequest(
        @Positive(message = "Target amount must be positive")
        BigDecimal targetAmount,

        @Future(message = "Target date must be in the future")
        LocalDate targetDate
) {
}
