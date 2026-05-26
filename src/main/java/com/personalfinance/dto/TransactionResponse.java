package com.personalfinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.personalfinance.entity.CategoryType;
import com.personalfinance.entity.TransactionEntity;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        LocalDate date,
        String category,
        String description,
        CategoryType type
) {

    public static TransactionResponse from(TransactionEntity transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getCategory().getName(),
                transaction.getDescription(),
                transaction.getCategory().getType());
    }
}
