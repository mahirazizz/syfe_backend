package com.syfe.pfm.repository;

import java.util.List;

import com.syfe.pfm.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(String type);
    java.util.Optional<Category> findByNameAndUserId(String name, Long userId);
    java.util.Optional<Category> findByNameAndCustomFalse(String name);
}
