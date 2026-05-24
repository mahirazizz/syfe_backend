package com.example.finance.controller;

import com.example.finance.model.Category;
import com.example.finance.model.Transaction;
import com.example.finance.model.User;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.TransactionRepository;
import com.example.finance.repository.UserRepository;
import com.example.finance.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private TransactionController controller;

    @Test
    void createRejectsNonPositiveAmount() {
        Transaction request = new Transaction();
        request.setAmount(BigDecimal.ZERO);

        ResponseEntity<?> response = controller.create(request, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Amount must be positive", response.getBody());
    }

    @Test
    void createRejectsFutureDate() {
        Transaction request = new Transaction();
        request.setAmount(new BigDecimal("10"));
        request.setDate(LocalDate.now().plusDays(1));

        ResponseEntity<?> response = controller.create(request, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date", response.getBody());
    }

    @Test
    void createRejectsInvalidCategory() {
        User user = new User();
        user.setUsername("user@example.com");
        Category category = new Category();
        category.setId(5L);
        Transaction request = new Transaction();
        request.setAmount(new BigDecimal("10"));
        request.setDate(LocalDate.now());
        request.setCategory(category);
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.create(request, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid category", response.getBody());
    }

    @Test
    void createReturnsCreatedTransaction() {
        User user = new User();
        user.setUsername("user@example.com");
        Category requestCategory = new Category();
        requestCategory.setId(5L);
        Category storedCategory = new Category();
        storedCategory.setId(5L);
        Transaction request = new Transaction();
        request.setAmount(new BigDecimal("10"));
        request.setDate(LocalDate.now());
        request.setCategory(requestCategory);
        Transaction saved = new Transaction();
        saved.setAmount(request.getAmount());
        saved.setDate(request.getDate());
        saved.setCategory(storedCategory);
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(storedCategory));
        when(transactionService.create(request)).thenReturn(saved);

        ResponseEntity<?> response = controller.create(request, principal);

        assertEquals(HttpStatus.valueOf(201), response.getStatusCode());
        assertSame(saved, response.getBody());
        assertSame(user, request.getUser());
        assertSame(storedCategory, request.getCategory());
        verify(transactionService).create(request);
    }

    @Test
    void listReturnsAllTransactionsWhenNoDateRangeProvided() {
        User user = new User();
        user.setUsername("user@example.com");
        Transaction tx = new Transaction();
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(transactionService.getAll(user)).thenReturn(List.of(tx));

        ResponseEntity<?> response = controller.list(null, null, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(tx), response.getBody());
        verify(transactionService).getAll(user);
    }

    @Test
    void listReturnsDateFilteredTransactionsWhenRangeProvided() {
        User user = new User();
        user.setUsername("user@example.com");
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        Transaction tx = new Transaction();
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(transactionService.getBetween(user, start, end)).thenReturn(List.of(tx));

        ResponseEntity<?> response = controller.list(start, end, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(tx), response.getBody());
        verify(transactionService).getBetween(user, start, end);
    }

    @Test
    void updateReturnsNotFoundForMissingTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.update(1L, new Transaction(), principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateReturnsForbiddenForDifferentUser() {
        User owner = new User();
        owner.setUsername("owner@example.com");
        Transaction tx = new Transaction();
        tx.setUser(owner);
        when(principal.getName()).thenReturn("other@example.com");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));

        ResponseEntity<?> response = controller.update(1L, new Transaction(), principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateAppliesEditableFields() {
        User owner = new User();
        owner.setUsername("owner@example.com");
        Transaction tx = new Transaction();
        tx.setUser(owner);
        tx.setAmount(new BigDecimal("10"));
        tx.setDescription("old");
        tx.setDate(LocalDate.now());
        Transaction request = new Transaction();
        request.setAmount(new BigDecimal("25"));
        request.setDescription("new");
        when(principal.getName()).thenReturn(owner.getUsername());
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(tx)).thenReturn(tx);

        ResponseEntity<?> response = controller.update(1L, request, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("25"), tx.getAmount());
        assertEquals("new", tx.getDescription());
        assertTrue(response.getBody() instanceof Transaction);
        verify(transactionRepository).save(tx);
    }

    @Test
    void deleteReturnsNotFoundForMissingTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.delete(1L, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteReturnsForbiddenForDifferentUser() {
        User owner = new User();
        owner.setUsername("owner@example.com");
        Transaction tx = new Transaction();
        tx.setUser(owner);
        when(principal.getName()).thenReturn("other@example.com");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));

        ResponseEntity<?> response = controller.delete(1L, principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void deleteMarksTransactionDeleted() {
        User owner = new User();
        owner.setUsername("owner@example.com");
        Transaction tx = new Transaction();
        tx.setUser(owner);
        tx.setDeleted(false);
        when(principal.getName()).thenReturn(owner.getUsername());
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(tx)).thenReturn(tx);

        ResponseEntity<?> response = controller.delete(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transaction deleted successfully", response.getBody());
        assertTrue(tx.isDeleted());
        verify(transactionRepository).save(tx);
    }
}
