package com.example.finance.service;

import com.example.finance.model.Goal;
import com.example.finance.model.User;
import com.example.finance.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal create(Goal goal) { return goalRepository.save(goal); }
    public List<Goal> getByUser(User user) { return goalRepository.findByUser(user); }
}
