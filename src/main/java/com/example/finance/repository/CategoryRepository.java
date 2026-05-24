package com.example.finance.repository;

import com.example.finance.model.Category;
import com.example.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserOrUserIsNull(User user);
    Optional<Category> findByNameAndUser(String name, User user);
}
