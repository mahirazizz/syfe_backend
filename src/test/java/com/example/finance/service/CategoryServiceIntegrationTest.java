package com.example.finance.service;

import com.example.finance.model.Category;
import com.example.finance.model.CategoryType;
import com.example.finance.model.Transaction;
import com.example.finance.model.User;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.TransactionRepository;
import com.example.finance.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@Transactional
public class CategoryServiceIntegrationTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    public void createDuplicateCategoryThrows() {
        User u = new User();
        u.setUsername("tuser@example.com"); u.setPassword("pwd"); u.setFullName("T");
        userRepository.save(u);

        Category c = new Category();
        c.setName("SideJob"); c.setType(CategoryType.INCOME);
        categoryService.createCustom(c, u);

        Category dup = new Category(); dup.setName("SideJob"); dup.setType(CategoryType.INCOME);
        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.createCustom(dup, u));
    }

    @Test
    public void deleteCategoryReferencedByTransactionThrows() {
        User u = new User(); u.setUsername("u2@example.com"); u.setPassword("pwd"); userRepository.save(u);
        Category c = new Category(); c.setName("G1"); c.setType(CategoryType.EXPENSE);
        Category saved = categoryService.createCustom(c, u);

        Transaction t = new Transaction();
        t.setAmount(new BigDecimal("100")); t.setDate(LocalDate.now()); t.setCategory(saved); t.setUser(u);
        transactionRepository.save(t);

        Assertions.assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory(saved.getId()));
    }

    @Test
    public void deleteUnusedCategorySucceeds() {
        User u = new User(); u.setUsername("u3@example.com"); u.setPassword("pwd"); userRepository.save(u);
        Category c = new Category(); c.setName("G2"); c.setType(CategoryType.EXPENSE);
        Category saved = categoryService.createCustom(c, u);

        categoryService.deleteCategory(saved.getId());
        Assertions.assertFalse(categoryRepository.findById(saved.getId()).isPresent());
    }
}
