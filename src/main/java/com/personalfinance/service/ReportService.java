package com.personalfinance.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personalfinance.dto.ReportResponse;
import com.personalfinance.entity.CategoryType;
import com.personalfinance.entity.UserEntity;
import com.personalfinance.exception.BadRequestException;
import com.personalfinance.repository.TransactionRepository;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public ReportResponse monthlyReport(int year, int month, UserEntity user) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return report(month, year, startDate, endDate, user);
    }

    @Transactional(readOnly = true)
    public ReportResponse yearlyReport(int year, UserEntity user) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        return report(null, year, startDate, endDate, user);
    }

    private ReportResponse report(Integer month, Integer year, LocalDate startDate, LocalDate endDate, UserEntity user) {
        Map<String, BigDecimal> income = totalsByCategory(user, CategoryType.INCOME, startDate, endDate);
        Map<String, BigDecimal> expenses = totalsByCategory(user, CategoryType.EXPENSE, startDate, endDate);

        BigDecimal totalIncome = income.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = expenses.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReportResponse(month, year, income, expenses, totalIncome.subtract(totalExpenses));
    }

    private Map<String, BigDecimal> totalsByCategory(UserEntity user, CategoryType type,
                                                     LocalDate startDate, LocalDate endDate) {
        List<Object[]> rows = transactionRepository.sumByCategoryForPeriod(user, type, startDate, endDate);
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (Object[] row : rows) {
            totals.put((String) row[0], (BigDecimal) row[1]);
        }
        return totals;
    }
}
