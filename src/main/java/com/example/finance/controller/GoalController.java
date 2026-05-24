package com.example.finance.controller;

import com.example.finance.model.Goal;
import com.example.finance.model.User;
import com.example.finance.repository.UserRepository;
import com.example.finance.service.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goalService;
    private final UserRepository userRepository;

    public GoalController(GoalService goalService, UserRepository userRepository) {
        this.goalService = goalService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Goal goal, Principal principal) {
        if (goal.getTargetAmount() == null || goal.getTargetAmount().doubleValue() <= 0) return ResponseEntity.badRequest().body("Invalid amount");
        if (goal.getTargetDate() == null || !goal.getTargetDate().isAfter(LocalDate.now())) return ResponseEntity.badRequest().body("Target date must be future");
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (goal.getStartDate() == null) goal.setStartDate(LocalDate.now());
        goal.setUser(user);
        Goal saved = goalService.create(goal);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    public ResponseEntity<?> getAll(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        return ResponseEntity.ok().body(goalService.getByUser(user));
    }
}
