package com.personalfinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GoalCreateRequest(
        @NotBlank(message = "Goal name is required")
        String goalName,

        @NotNull(message = "Target amount is required")
        @Positive(message = "Target amount must be positive")
        BigDecimal targetAmount,

        @NotNull(message = "Target date is required")
        @Future(message = "Target date must be in the future")
        LocalDate targetDate,

        LocalDate startDate
) {
}
