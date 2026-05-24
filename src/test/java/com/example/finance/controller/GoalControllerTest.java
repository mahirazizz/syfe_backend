package com.example.finance.controller;

import com.example.finance.model.Goal;
import com.example.finance.model.User;
import com.example.finance.repository.UserRepository;
import com.example.finance.service.GoalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    private GoalService goalService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private GoalController controller;

    @Test
    void createRejectsNonPositiveAmount() {
        Goal goal = new Goal();
        goal.setTargetAmount(BigDecimal.ZERO);

        ResponseEntity<?> response = controller.create(goal, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid amount", response.getBody());
    }

    @Test
    void createRejectsPastTargetDate() {
        Goal goal = new Goal();
        goal.setTargetAmount(new BigDecimal("100"));
        goal.setTargetDate(LocalDate.now().minusDays(1));

        ResponseEntity<?> response = controller.create(goal, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Target date must be future", response.getBody());
    }

    @Test
    void createSetsStartDateAndReturnsCreatedGoal() {
        User user = new User();
        user.setUsername("user@example.com");
        Goal goal = new Goal();
        goal.setGoalName("Emergency fund");
        goal.setTargetAmount(new BigDecimal("1000"));
        goal.setTargetDate(LocalDate.now().plusDays(30));
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(goalService.create(goal)).thenReturn(goal);

        ResponseEntity<?> response = controller.create(goal, principal);

        assertEquals(HttpStatus.valueOf(201), response.getStatusCode());
        assertEquals(goal, response.getBody());
        assertEquals(LocalDate.now(), goal.getStartDate());
        assertEquals(user, goal.getUser());
        verify(goalService).create(goal);
    }

    @Test
    void getAllReturnsGoalsForUser() {
        User user = new User();
        user.setUsername("user@example.com");
        Goal goal = new Goal();
        goal.setGoalName("Trip");
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(goalService.getByUser(user)).thenReturn(List.of(goal));

        ResponseEntity<?> response = controller.getAll(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(goal), response.getBody());
        verify(goalService).getByUser(user);
    }
}
