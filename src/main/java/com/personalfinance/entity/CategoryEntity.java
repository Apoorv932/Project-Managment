package com.personalfinance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_category_name", columnNames = {"user_id", "name"})
)
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @Column(name = "is_custom", nullable = false)
    private boolean custom;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    protected CategoryEntity() {
    }

    private CategoryEntity(String name, CategoryType type, boolean custom, UserEntity user) {
        this.name = name;
        this.type = type;
        this.custom = custom;
        this.user = user;
        this.deleted = false;
    }

    public static CategoryEntity defaultCategory(String name, CategoryType type) {
        return new CategoryEntity(name, type, false, null);
    }

    public static CategoryEntity customCategory(String name, CategoryType type, UserEntity user) {
        return new CategoryEntity(name, type, true, user);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }

    public boolean isCustom() {
        return custom;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public UserEntity getUser() {
        return user;
    }

}
