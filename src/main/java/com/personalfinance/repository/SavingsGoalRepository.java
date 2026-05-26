package com.personalfinance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personalfinance.entity.SavingsGoalEntity;
import com.personalfinance.entity.UserEntity;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, Long> {

    List<SavingsGoalEntity> findByUserOrderByTargetDateAscIdAsc(UserEntity user);

    Optional<SavingsGoalEntity> findByIdAndUser(Long id, UserEntity user);
}
