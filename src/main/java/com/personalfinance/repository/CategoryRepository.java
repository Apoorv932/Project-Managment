package com.personalfinance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.personalfinance.entity.CategoryEntity;
import com.personalfinance.entity.UserEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByNameAndCustomFalse(String name);

    Optional<CategoryEntity> findByNameAndCustomFalseAndDeletedFalse(String name);

    Optional<CategoryEntity> findByNameAndUserAndDeletedFalse(String name, UserEntity user);

    boolean existsByNameAndUserAndDeletedFalse(String name, UserEntity user);

    @Query("""
            select c from CategoryEntity c
            where c.deleted = false
              and (c.custom = false or c.user = :user)
            order by c.type asc, c.name asc
            """)
    List<CategoryEntity> findAccessibleCategories(@Param("user") UserEntity user);
}
