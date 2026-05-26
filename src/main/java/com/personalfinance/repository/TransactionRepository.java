package com.personalfinance.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.personalfinance.entity.CategoryEntity;
import com.personalfinance.entity.CategoryType;
import com.personalfinance.entity.TransactionEntity;
import com.personalfinance.entity.UserEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    Optional<TransactionEntity> findByIdAndUser(Long id, UserEntity user);

    boolean existsByCategoryAndUser(CategoryEntity category, UserEntity user);

    @Query("""
            select t from TransactionEntity t
            where t.user = :user
              and (:startDate is null or t.transactionDate >= :startDate)
              and (:endDate is null or t.transactionDate <= :endDate)
              and (:category is null or t.category = :category)
              and (:type is null or t.category.type = :type)
            order by t.transactionDate desc, t.id desc
            """)
    List<TransactionEntity> findFiltered(
            @Param("user") UserEntity user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("category") CategoryEntity category,
            @Param("type") CategoryType type);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from TransactionEntity t
            where t.user = :user
              and t.category.type = :type
              and t.transactionDate >= :startDate
            """)
    BigDecimal sumAmountByTypeSince(
            @Param("user") UserEntity user,
            @Param("type") CategoryType type,
            @Param("startDate") LocalDate startDate);

    @Query("""
            select t.category.name, coalesce(sum(t.amount), 0)
            from TransactionEntity t
            where t.user = :user
              and t.category.type = :type
              and t.transactionDate >= :startDate
              and t.transactionDate <= :endDate
            group by t.category.name
            order by t.category.name asc
            """)
    List<Object[]> sumByCategoryForPeriod(
            @Param("user") UserEntity user,
            @Param("type") CategoryType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
