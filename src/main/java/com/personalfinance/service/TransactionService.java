package com.personalfinance.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personalfinance.dto.MessageResponse;
import com.personalfinance.dto.TransactionCreateRequest;
import com.personalfinance.dto.TransactionResponse;
import com.personalfinance.dto.TransactionUpdateRequest;
import com.personalfinance.dto.TransactionsResponse;
import com.personalfinance.entity.CategoryEntity;
import com.personalfinance.entity.CategoryType;
import com.personalfinance.entity.TransactionEntity;
import com.personalfinance.entity.UserEntity;
import com.personalfinance.exception.BadRequestException;
import com.personalfinance.exception.NotFoundException;
import com.personalfinance.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest request, UserEntity user) {
        if (request.date().isAfter(LocalDate.now())) {
            throw new BadRequestException("Date cannot be in the future");
        }

        CategoryEntity category = categoryService.findAccessibleCategoryByName(request.category(), user);
        TransactionEntity transaction = new TransactionEntity(
                request.amount(),
                request.date(),
                category,
                request.description(),
                user);

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public TransactionsResponse getTransactions(LocalDate startDate, LocalDate endDate, Long categoryId,
                                                CategoryType type, UserEntity user) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        CategoryEntity category = null;
        if (categoryId != null) {
            category = categoryService.findAccessibleCategoryById(categoryId, user);
        }

        List<TransactionResponse> transactions = transactionRepository
                .findFiltered(user, startDate, endDate, category, type)
                .stream()
                .map(TransactionResponse::from)
                .toList();

        return new TransactionsResponse(transactions);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, UserEntity user) {
        TransactionEntity transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        CategoryEntity category = null;
        if (request.category() != null && !request.category().isBlank()) {
            category = categoryService.findAccessibleCategoryByName(request.category(), user);
        }

        transaction.update(request.amount(), category, request.description());
        return TransactionResponse.from(transaction);
    }

    @Transactional
    public MessageResponse deleteTransaction(Long id, UserEntity user) {
        TransactionEntity transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        transactionRepository.delete(transaction);
        return new MessageResponse("Transaction deleted successfully");
    }
}
