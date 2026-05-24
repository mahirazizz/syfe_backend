package com.example.finance.controller;

import com.example.finance.model.Category;
import com.example.finance.model.User;
import com.example.finance.repository.UserRepository;
import com.example.finance.security.CustomUserDetailsService;
import com.example.finance.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public CategoryController(CategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        List<Category> list = categoryService.getAllForUser(user);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Category category, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        Category saved = categoryService.createCustom(category, user);
        return ResponseEntity.status(201).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        Category cat = categoryService.getAllForUser(user).stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        if (cat == null) return ResponseEntity.status(404).body("Category not found");
        if (cat.getUser() == null || !cat.getUser().getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().body("Category deleted successfully");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }
}
