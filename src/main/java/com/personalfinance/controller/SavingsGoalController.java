package com.personalfinance.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personalfinance.dto.GoalCreateRequest;
import com.personalfinance.dto.GoalResponse;
import com.personalfinance.dto.GoalUpdateRequest;
import com.personalfinance.dto.GoalsResponse;
import com.personalfinance.dto.MessageResponse;
import com.personalfinance.security.FinanceUserPrincipal;
import com.personalfinance.service.SavingsGoalService;

@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalCreateRequest request,
                                                   @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savingsGoalService.createGoal(request, principal.getUser()));
    }

    @GetMapping
    public GoalsResponse getGoals(@AuthenticationPrincipal FinanceUserPrincipal principal) {
        return savingsGoalService.getGoals(principal.getUser());
    }

    @GetMapping("/{id}")
    public GoalResponse getGoal(@PathVariable Long id, @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return savingsGoalService.getGoal(id, principal.getUser());
    }

    @PutMapping("/{id}")
    public GoalResponse updateGoal(@PathVariable Long id,
                                   @Valid @RequestBody GoalUpdateRequest request,
                                   @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return savingsGoalService.updateGoal(id, request, principal.getUser());
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteGoal(@PathVariable Long id,
                                      @AuthenticationPrincipal FinanceUserPrincipal principal) {
        return savingsGoalService.deleteGoal(id, principal.getUser());
    }
}
