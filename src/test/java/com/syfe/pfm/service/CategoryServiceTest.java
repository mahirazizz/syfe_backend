package com.syfe.pfm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.syfe.pfm.dto.request.CategoryRequest;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    com.syfe.pfm.repository.TransactionRepository transactionRepository;

    @InjectMocks
    CategoryService categoryService;

    @Test
    void createForUserSavesCategory() {
        CategoryRequest req = new CategoryRequest("Custom", "EXPENSE");
        Long userId = 2L;

        when(categoryRepository.findByNameAndUserId("Custom", userId)).thenReturn(Optional.empty());

        Category saved = new Category();
        saved.setId(7L);
        saved.setName("Custom");
        saved.setType("EXPENSE");

        when(categoryRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(saved);

        Category out = categoryService.createForUser(req, userId);
        assertEquals("Custom", out.getName());
        assertEquals("EXPENSE", out.getType());
    }
}
