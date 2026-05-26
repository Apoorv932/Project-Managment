package com.personalfinance.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personalfinance.dto.CategoriesResponse;
import com.personalfinance.dto.CategoryRequest;
import com.personalfinance.dto.CategoryResponse;
import com.personalfinance.dto.MessageResponse;
import com.personalfinance.entity.CategoryEntity;
import com.personalfinance.entity.UserEntity;
import com.personalfinance.exception.BadRequestException;
import com.personalfinance.exception.ConflictException;
import com.personalfinance.exception.ForbiddenException;
import com.personalfinance.exception.NotFoundException;
import com.personalfinance.repository.CategoryRepository;
import com.personalfinance.repository.TransactionRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public CategoriesResponse getCategories(UserEntity user) {
        List<CategoryResponse> categories = categoryRepository.findAccessibleCategories(user).stream()
                .map(CategoryResponse::from)
                .toList();
        return new CategoriesResponse(categories);
    }

    @Transactional
    public CategoryResponse createCustomCategory(CategoryRequest request, UserEntity user) {
        String name = normalizeName(request.name());
        if (categoryRepository.existsByNameAndUserAndDeletedFalse(name, user)
                || categoryRepository.findByNameAndCustomFalseAndDeletedFalse(name).isPresent()) {
            throw new ConflictException("Category already exists");
        }

        CategoryEntity category = CategoryEntity.customCategory(name, request.type(), user);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public MessageResponse deleteCustomCategory(String name, UserEntity user) {
        CategoryEntity category = categoryRepository.findByNameAndUserAndDeletedFalse(name, user)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (!category.isCustom()) {
            throw new ForbiddenException("Default categories cannot be deleted");
        }
        if (transactionRepository.existsByCategoryAndUser(category, user)) {
            throw new BadRequestException("Category is referenced by transactions");
        }

        categoryRepository.delete(category);
        return new MessageResponse("Category deleted successfully");
    }

    @Transactional(readOnly = true)
    public CategoryEntity findAccessibleCategoryByName(String name, UserEntity user) {
        String normalizedName = normalizeName(name);
        return categoryRepository.findByNameAndUserAndDeletedFalse(normalizedName, user)
                .or(() -> categoryRepository.findByNameAndCustomFalseAndDeletedFalse(normalizedName))
                .orElseThrow(() -> new BadRequestException("Invalid category"));
    }

    @Transactional(readOnly = true)
    public CategoryEntity findAccessibleCategoryById(Long id, UserEntity user) {
        return categoryRepository.findById(id)
                .filter(category -> !category.isDeleted())
                .filter(category -> !category.isCustom() || category.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BadRequestException("Invalid category"));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Category name is required");
        }
        return name.trim();
    }
}
