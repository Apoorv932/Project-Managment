package com.personalfinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.personalfinance.entity.SavingsGoalEntity;

public record GoalResponse(
        Long id,
        String goalName,
        BigDecimal targetAmount,
        LocalDate targetDate,
        LocalDate startDate,
        BigDecimal currentProgress,
        Double progressPercentage,
        BigDecimal remainingAmount
) {

    public static GoalResponse from(SavingsGoalEntity goal, BigDecimal currentProgress,
                                    Double progressPercentage, BigDecimal remainingAmount) {
        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getStartDate(),
                currentProgress,
                progressPercentage,
                remainingAmount);
    }
}
