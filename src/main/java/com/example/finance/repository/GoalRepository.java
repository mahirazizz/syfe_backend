package com.example.finance.repository;

import com.example.finance.model.Goal;
import com.example.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);
}
