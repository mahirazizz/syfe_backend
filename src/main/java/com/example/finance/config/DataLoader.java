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
        // create default categories if not present
        if (categoryRepository.count() == 0) {
            Category c = new Category();
            c.setName("Salary"); c.setType(CategoryType.INCOME); c.setCustom(false); c.setUser(null);
            categoryRepository.save(c);
            List<String> expenses = List.of("Food","Rent","Transportation","Entertainment","Healthcare","Utilities");
            for (String name : expenses) {
                Category e = new Category();
                e.setName(name); e.setType(CategoryType.EXPENSE); e.setCustom(false); e.setUser(null);
                categoryRepository.save(e);
            }
        }
    }
}
