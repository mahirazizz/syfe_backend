package com.example.finance.service;

import com.example.finance.model.Category;
import com.example.finance.model.CategoryType;
import com.example.finance.model.User;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService service;

    @Test
    void getAllForUserDelegatesToRepository() {
        User user = new User();
        user.setUsername("user@example.com");
        Category category = new Category();
        when(categoryRepository.findByUserOrUserIsNull(user)).thenReturn(List.of(category));

        List<Category> result = service.getAllForUser(user);

        assertEquals(List.of(category), result);
        verify(categoryRepository).findByUserOrUserIsNull(user);
    }

    @Test
    void createCustomRejectsDuplicateCategoryForUser() {
        User user = new User();
        user.setUsername("user@example.com");
        Category category = new Category();
        category.setName("Travel");
        when(categoryRepository.findByNameAndUser("Travel", user)).thenReturn(Optional.of(category));

        assertThrows(IllegalArgumentException.class, () -> service.createCustom(category, user));
    }

    @Test
    void createCustomMarksCategoryAsCustomAndAssignsUser() {
        User user = new User();
        user.setUsername("user@example.com");
        Category category = new Category();
        category.setName("Travel");
        category.setType(CategoryType.EXPENSE);
        when(categoryRepository.findByNameAndUser("Travel", user)).thenReturn(Optional.empty());
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = service.createCustom(category, user);

        assertSame(category, result);
        assertTrue(category.isCustom());
        assertSame(user, category.getUser());
        verify(categoryRepository).save(category);
    }

    @Test
    void deleteCategoryRejectsMissingCategory() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.deleteCategory(99L));
    }

    @Test
    void deleteCategoryRejectsDefaultCategory() {
        Category category = new Category();
        category.setCustom(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(IllegalArgumentException.class, () -> service.deleteCategory(1L));
    }

    @Test
    void deleteCategoryRejectsCategoryUsedByTransactions() {
        Category category = new Category();
        category.setCustom(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryAndDeletedFalse(category)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.deleteCategory(1L));
    }

    @Test
    void deleteCategoryDeletesUnusedCustomCategory() {
        Category category = new Category();
        category.setCustom(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryAndDeletedFalse(category)).thenReturn(false);

        service.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }
}
