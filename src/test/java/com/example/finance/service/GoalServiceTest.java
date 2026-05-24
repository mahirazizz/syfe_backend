package com.example.finance.service;

import com.example.finance.model.Goal;
import com.example.finance.model.User;
import com.example.finance.repository.GoalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService service;

    @Test
    void createDelegatesToRepository() {
        Goal goal = new Goal();
        goal.setGoalName("Emergency fund");
        when(goalRepository.save(goal)).thenReturn(goal);

        Goal result = service.create(goal);

        assertSame(goal, result);
        verify(goalRepository).save(goal);
    }

    @Test
    void getByUserDelegatesToRepository() {
        User user = new User();
        user.setUsername("user@example.com");
        Goal goal = new Goal();
        when(goalRepository.findByUser(user)).thenReturn(List.of(goal));

        List<Goal> result = service.getByUser(user);

        assertEquals(List.of(goal), result);
        verify(goalRepository).findByUser(user);
    }
}
