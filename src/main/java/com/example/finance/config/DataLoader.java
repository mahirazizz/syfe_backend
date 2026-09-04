package com.example.finance.config;

import com.example.finance.model.Category;
import com.example.finance.model.CategoryType;
import com.example.finance.repository.CategoryRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader {
    private final CategoryRepository categoryRepository;

    public DataLoader(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        createDefaultCategory("Salary", CategoryType.INCOME);
        List<String> expenses = List.of("Food", "Rent", "Transportation", "Entertainment", "Healthcare", "Utilities");
        expenses.forEach(name -> createDefaultCategory(name, CategoryType.EXPENSE));
    }

    private void createDefaultCategory(String name, CategoryType type) {
        if (categoryRepository.findByNameAndUserIsNull(name).isPresent()) {
            return;
        }

        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setCustom(false);
        category.setUser(null);
        categoryRepository.save(category);
    }
}
