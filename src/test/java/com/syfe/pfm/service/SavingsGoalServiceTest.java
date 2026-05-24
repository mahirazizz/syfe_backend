package com.syfe.pfm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.syfe.pfm.entity.SavingsGoal;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.repository.SavingsGoalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SavingsGoalServiceTest {

    @Mock
    SavingsGoalRepository savingsGoalRepository;

    @Mock
    UserService userService;

    @Mock
    com.syfe.pfm.repository.TransactionRepository transactionRepository;

    @InjectMocks
    SavingsGoalService savingsGoalService;

    @Test
    void toResponseCalculatesProgress() {
        User u = new User();
        u.setId(3L);

        SavingsGoal goal = new SavingsGoal();
        goal.setId(1L);
        goal.setName("G");
        goal.setTargetAmount(new BigDecimal("1000"));
        goal.setCurrentAmount(new BigDecimal("0"));
        goal.setStartDate(LocalDate.of(2024,1,1));
        goal.setUser(u);

        Transaction t1 = new Transaction();
        t1.setType("INCOME");
        t1.setAmount(new BigDecimal("600"));
        t1.setTransactionDate(LocalDate.of(2024,2,1));

        Transaction t2 = new Transaction();
        t2.setType("EXPENSE");
        t2.setAmount(new BigDecimal("100"));
        t2.setTransactionDate(LocalDate.of(2024,2,2));

        when(transactionRepository.findByUserIdAndTransactionDateBetweenAndDeletedFalseOrderByTransactionDateDesc(3L, goal.getStartDate(), LocalDate.now()))
                .thenReturn(List.of(t1, t2));

        var resp = savingsGoalService.toResponse(goal);

        assertEquals(new BigDecimal("500"), resp.currentProgress());
        assertEquals(new BigDecimal("500"), resp.remainingAmount());
    }
}
