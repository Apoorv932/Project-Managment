package com.personalfinance.dto;

import com.personalfinance.entity.CategoryEntity;
import com.personalfinance.entity.CategoryType;

public record CategoryResponse(String name, CategoryType type, boolean isCustom) {

    public static CategoryResponse from(CategoryEntity category) {
        return new CategoryResponse(category.getName(), category.getType(), category.isCustom());
    }
}
