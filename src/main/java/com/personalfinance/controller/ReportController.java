package com.personalfinance.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personalfinance.dto.ReportResponse;
import com.personalfinance.security.FinanceUserPrincipal;
import com.personalfinance.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly/{year}/{month}")
    public ReportResponse monthlyReport(@PathVariable int year, @PathVariable int month,
                                        @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return reportService.monthlyReport(year, month, principal.getUser());
    }

    @GetMapping("/yearly/{year}")
    public ReportResponse yearlyReport(@PathVariable int year,
                                       @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return reportService.yearlyReport(year, principal.getUser());
    }
}
