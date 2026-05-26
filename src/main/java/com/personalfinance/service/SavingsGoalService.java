package com.personalfinance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personalfinance.dto.GoalCreateRequest;
import com.personalfinance.dto.GoalResponse;
import com.personalfinance.dto.GoalUpdateRequest;
import com.personalfinance.dto.GoalsResponse;
import com.personalfinance.dto.MessageResponse;
import com.personalfinance.entity.CategoryType;
import com.personalfinance.entity.SavingsGoalEntity;
import com.personalfinance.entity.UserEntity;
import com.personalfinance.exception.BadRequestException;
import com.personalfinance.exception.ForbiddenException;
import com.personalfinance.exception.NotFoundException;
import com.personalfinance.repository.SavingsGoalRepository;
import com.personalfinance.repository.TransactionRepository;

@Service
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository,
                              TransactionRepository transactionRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public GoalResponse createGoal(GoalCreateRequest request, UserEntity user) {
        LocalDate startDate = request.startDate() == null ? LocalDate.now() : request.startDate();
        validateTargetDate(request.targetDate());

        SavingsGoalEntity goal = new SavingsGoalEntity(
                request.goalName().trim(),
                request.targetAmount(),
                request.targetDate(),
                startDate,
                user);

        return toResponse(savingsGoalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public GoalsResponse getGoals(UserEntity user) {
        List<GoalResponse> goals = savingsGoalRepository.findByUserOrderByTargetDateAscIdAsc(user).stream()
                .map(this::toResponse)
                .toList();
        return new GoalsResponse(goals);
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoal(Long id, UserEntity user) {
        return toResponse(findGoal(id, user));
    }

    @Transactional
    public GoalResponse updateGoal(Long id, GoalUpdateRequest request, UserEntity user) {
        SavingsGoalEntity goal = findGoal(id, user);
        if (request.targetDate() != null) {
            validateTargetDate(request.targetDate());
        }

        goal.update(request.targetAmount(), request.targetDate());
        return toResponse(goal);
    }

    @Transactional
    public MessageResponse deleteGoal(Long id, UserEntity user) {
        SavingsGoalEntity goal = findGoal(id, user);
        savingsGoalRepository.delete(goal);
        return new MessageResponse("Goal deleted successfully");
    }

    private SavingsGoalEntity findGoal(Long id, UserEntity user) {
        SavingsGoalEntity goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied");
        }
        return goal;
    }

    private GoalResponse toResponse(SavingsGoalEntity goal) {
        BigDecimal income = transactionRepository.sumAmountByTypeSince(
                goal.getUser(), CategoryType.INCOME, goal.getStartDate());
        BigDecimal expenses = transactionRepository.sumAmountByTypeSince(
                goal.getUser(), CategoryType.EXPENSE, goal.getStartDate());
        BigDecimal currentProgress = income.subtract(expenses);
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentProgress).max(BigDecimal.ZERO);
        BigDecimal progressPercentage = currentProgress
                .multiply(BigDecimal.valueOf(100))
                .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);

        return GoalResponse.from(goal, currentProgress, progressPercentage, remainingAmount);
    }

    private void validateTargetDate(LocalDate targetDate) {
        if (!targetDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Target date must be in the future");
        }
    }
}
