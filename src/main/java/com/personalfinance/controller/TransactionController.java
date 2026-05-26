package com.personalfinance.controller;

import java.time.LocalDate;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.personalfinance.dto.MessageResponse;
import com.personalfinance.dto.TransactionCreateRequest;
import com.personalfinance.dto.TransactionResponse;
import com.personalfinance.dto.TransactionUpdateRequest;
import com.personalfinance.dto.TransactionsResponse;
import com.personalfinance.entity.CategoryType;
import com.personalfinance.security.FinanceUserPrincipal;
import com.personalfinance.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionCreateRequest request,
                                                                @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(request, principal.getUser()));
    }

    @GetMapping
    public TransactionsResponse getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CategoryType type,
            @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return transactionService.getTransactions(startDate, endDate, categoryId, category, type, principal.getUser());
    }

    @PutMapping("/{id}")
    public TransactionResponse updateTransaction(@PathVariable Long id,
                                                 @Valid @RequestBody TransactionUpdateRequest request,
                                                 @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return transactionService.updateTransaction(id, request, principal.getUser());
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteTransaction(@PathVariable Long id,
                                             @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return transactionService.deleteTransaction(id, principal.getUser());
    }
}
