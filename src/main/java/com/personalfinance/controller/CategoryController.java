package com.personalfinance.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personalfinance.dto.CategoriesResponse;
import com.personalfinance.dto.CategoryRequest;
import com.personalfinance.dto.CategoryResponse;
import com.personalfinance.dto.MessageResponse;
import com.personalfinance.security.FinanceUserPrincipal;
import com.personalfinance.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public CategoriesResponse getCategories(@AuthenticationPrincipal FinanceUserPrincipal principal) {
        return categoryService.getCategories(principal.getUser());
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request,
                                                          @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCustomCategory(request, principal.getUser()));
    }

    @DeleteMapping("/{name}")
    public MessageResponse deleteCategory(@PathVariable String name,
                                          @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return categoryService.deleteCustomCategory(name, principal.getUser());
    }
}
