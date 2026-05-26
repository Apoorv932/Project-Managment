package com.personalfinance.dto;

import com.personalfinance.entity.CategoryEntity;
import com.personalfinance.entity.CategoryType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryResponse(
        String name,
        CategoryType type,
        @JsonProperty("isCustom") boolean isCustom,
        boolean custom
) {

    public static CategoryResponse from(CategoryEntity category) {
        return new CategoryResponse(category.getName(), category.getType(), category.isCustom(), category.isCustom());
    }
}
