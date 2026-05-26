package com.personalfinance.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ReportResponse(
        Integer month,
        Integer year,
        Map<String, BigDecimal> totalIncome,
        Map<String, BigDecimal> totalExpenses,
        BigDecimal netSavings
) {
}
