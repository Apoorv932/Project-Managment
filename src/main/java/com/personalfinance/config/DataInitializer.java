package com.personalfinance.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.personalfinance.entity.CategoryEntity;
import com.personalfinance.entity.CategoryType;
import com.personalfinance.repository.CategoryRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDefaultCategories(CategoryRepository categoryRepository) {
        return args -> {
            createDefaultCategoryIfMissing(categoryRepository, "Salary", CategoryType.INCOME);
            createDefaultCategoryIfMissing(categoryRepository, "Food", CategoryType.EXPENSE);
            createDefaultCategoryIfMissing(categoryRepository, "Rent", CategoryType.EXPENSE);
            createDefaultCategoryIfMissing(categoryRepository, "Transportation", CategoryType.EXPENSE);
            createDefaultCategoryIfMissing(categoryRepository, "Entertainment", CategoryType.EXPENSE);
            createDefaultCategoryIfMissing(categoryRepository, "Healthcare", CategoryType.EXPENSE);
            createDefaultCategoryIfMissing(categoryRepository, "Utilities", CategoryType.EXPENSE);
        };
    }

    private void createDefaultCategoryIfMissing(CategoryRepository categoryRepository, String name, CategoryType type) {
        if (!categoryRepository.existsByNameAndCustomFalse(name)) {
            categoryRepository.save(CategoryEntity.defaultCategory(name, type));
        }
    }
}
