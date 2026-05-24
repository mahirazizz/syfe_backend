package com.syfe.pfm.controller;

import java.util.List;

import com.syfe.pfm.dto.request.GoalRequest;
import com.syfe.pfm.dto.request.UpdateGoalRequest;
import com.syfe.pfm.entity.SavingsGoal;
import com.syfe.pfm.service.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final SavingsGoalService savingsGoalService;

    public GoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @GetMapping
    public java.util.Map<String, Object> getAll(javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        var goals = savingsGoalService.findAll();
        java.util.List<com.syfe.pfm.dto.response.GoalResponse> resp = new java.util.ArrayList<>();
        for (var g : goals) {
            if (g.getUser().getId().equals(userId)) {
                resp.add(savingsGoalService.toResponse(g));
            }
        }
        var body = new java.util.HashMap<String, Object>();
        body.put("goals", resp);
        return body;
    }

    @PostMapping
    public ResponseEntity<com.syfe.pfm.dto.response.GoalResponse> create(@Valid @RequestBody GoalRequest request, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        var goal = savingsGoalService.create(request, userId);
        return ResponseEntity.status(201).body(savingsGoalService.toResponse(goal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.syfe.pfm.dto.response.GoalResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateGoalRequest request, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        var goal = savingsGoalService.getById(id);
        if (!goal.getUser().getId().equals(userId)) throw new com.syfe.pfm.exception.UnauthorizedException("Not allowed");
        var updated = savingsGoalService.update(id, request);
        return ResponseEntity.ok(savingsGoalService.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<com.syfe.pfm.dto.response.MessageResponse> delete(@PathVariable Long id, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        var goal = savingsGoalService.getById(id);
        if (!goal.getUser().getId().equals(userId)) throw new com.syfe.pfm.exception.UnauthorizedException("Not allowed");
        savingsGoalService.delete(id);
        return ResponseEntity.ok(new com.syfe.pfm.dto.response.MessageResponse("Goal deleted successfully"));
    }
}
