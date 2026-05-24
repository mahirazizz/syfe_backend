package com.syfe.pfm.service;

import java.util.List;

import com.syfe.pfm.dto.request.GoalRequest;
import com.syfe.pfm.dto.request.UpdateGoalRequest;
import com.syfe.pfm.entity.SavingsGoal;
import com.syfe.pfm.exception.ResourceNotFoundException;
import com.syfe.pfm.repository.SavingsGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserService userService;
    private final com.syfe.pfm.repository.TransactionRepository transactionRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository, UserService userService, com.syfe.pfm.repository.TransactionRepository transactionRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    public SavingsGoal create(GoalRequest request, Long userId) {
        SavingsGoal goal = new SavingsGoal();
        goal.setName(request.goalName());
        goal.setTargetAmount(request.targetAmount());
        goal.setCurrentAmount(request.currentAmount());
        if (request.targetDate().isBefore(java.time.LocalDate.now().plusDays(1))) {
            throw new com.syfe.pfm.exception.BadRequestException("Target date must be a future date");
        }
        goal.setTargetDate(request.targetDate());
        goal.setStartDate(request.startDate() != null ? request.startDate() : java.time.LocalDate.now());
        goal.setUser(userService.getById(userId));
        return savingsGoalRepository.save(goal);
    }

    public SavingsGoal update(Long id, UpdateGoalRequest request) {
        SavingsGoal goal = getById(id);
        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        goal.setCurrentAmount(request.currentAmount());
        goal.setTargetDate(request.targetDate());
        return savingsGoalRepository.save(goal);
    }

    public com.syfe.pfm.dto.response.GoalResponse toResponse(SavingsGoal goal) {
        Long userId = goal.getUser().getId();
        java.time.LocalDate start = goal.getStartDate();
        java.time.LocalDate end = java.time.LocalDate.now();
        var txs = transactionRepository.findByUserIdAndTransactionDateBetweenAndDeletedFalseOrderByTransactionDateDesc(userId, start, end);
        java.math.BigDecimal income = java.math.BigDecimal.ZERO;
        java.math.BigDecimal expense = java.math.BigDecimal.ZERO;
        for (var t : txs) {
            if ("INCOME".equalsIgnoreCase(t.getType())) income = income.add(t.getAmount());
            else expense = expense.add(t.getAmount());
        }
        java.math.BigDecimal currentProgress = income.subtract(expense);
        if (currentProgress.compareTo(java.math.BigDecimal.ZERO) < 0) currentProgress = java.math.BigDecimal.ZERO;
        java.math.BigDecimal remaining = goal.getTargetAmount().subtract(currentProgress);
        if (remaining.compareTo(java.math.BigDecimal.ZERO) < 0) remaining = java.math.BigDecimal.ZERO;
        double percent = 0.0;
        if (goal.getTargetAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            percent = currentProgress.divide(goal.getTargetAmount(), 4, java.math.RoundingMode.HALF_UP).multiply(new java.math.BigDecimal("100")).doubleValue();
        }
        return new com.syfe.pfm.dto.response.GoalResponse(goal.getId(), goal.getName(), goal.getTargetAmount(), goal.getTargetDate(), goal.getStartDate(), currentProgress, percent, remaining);
    }

    @Transactional(readOnly = true)
    public List<SavingsGoal> findAll() {
        return savingsGoalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SavingsGoal getById(Long id) {
        return savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found"));
    }

    public void delete(Long id) {
        savingsGoalRepository.delete(getById(id));
    }
}
