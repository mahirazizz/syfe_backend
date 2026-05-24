package com.example.finance.service;

import com.example.finance.model.Category;
import com.example.finance.model.User;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Category> getAllForUser(User user) {
        return categoryRepository.findByUserOrUserIsNull(user);
    }

    public Category createCustom(Category category, User user) {
        // validate unique per user
        if (categoryRepository.findByNameAndUser(category.getName(), user).isPresent()) {
            throw new IllegalArgumentException("Category name already exists for user");
        }
        category.setCustom(true);
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId) {
        Category cat = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        if (!cat.isCustom()) {
            throw new IllegalArgumentException("Default categories cannot be deleted");
        }
        if (transactionRepository.existsByCategoryAndDeletedFalse(cat)) {
            throw new IllegalStateException("Category is referenced by transactions");
        }
        categoryRepository.delete(cat);
    }
}
