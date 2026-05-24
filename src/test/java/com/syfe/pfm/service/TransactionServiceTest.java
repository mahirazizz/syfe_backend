package com.syfe.pfm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.syfe.pfm.dto.request.TransactionRequest;
import com.syfe.pfm.dto.request.UpdateTransactionRequest;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    UserService userService;

    @Mock
    CategoryService categoryService;

    @InjectMocks
    TransactionService transactionService;

    @Test
    void createSetsFieldsAndSaves() {
        Long userId = 1L;
        TransactionRequest req = new TransactionRequest(new BigDecimal("100"), "desc", "INCOME", LocalDate.now(), "Salary");

        Category cat = new Category();
        cat.setId(2L);
        cat.setName("Salary");

        User u = new User();
        u.setId(userId);

        when(categoryService.findByNameForUserOrDefault("Salary", userId)).thenReturn(java.util.Optional.of(cat));
        when(userService.getById(userId)).thenReturn(u);

        Transaction saved = new Transaction();
        saved.setId(10L);
        saved.setAmount(req.amount());
        saved.setDescription(req.description());
        saved.setType(req.type());
        saved.setTransactionDate(req.date());
        saved.setCategory(cat);
        saved.setUser(u);

        when(transactionRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(saved);

        Transaction out = transactionService.create(req, userId);
        assertEquals(saved.getId(), out.getId());
        assertEquals(new BigDecimal("100"), out.getAmount());
    }

    @Test
    void updateUnauthorizedThrows() {
        Long userId = 1L;
        Long otherUser = 2L;

        Transaction existing = new Transaction();
        existing.setId(5L);
        User owner = new User();
        owner.setId(otherUser);
        existing.setUser(owner);

        when(transactionRepository.findById(5L)).thenReturn(Optional.of(existing));

        UpdateTransactionRequest req = new UpdateTransactionRequest(new BigDecimal("10"), "x");

        assertThrows(com.syfe.pfm.exception.UnauthorizedException.class, () -> transactionService.update(5L, req, userId));
    }
}
