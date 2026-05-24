package com.example.finance.service;

import com.example.finance.model.Transaction;
import com.example.finance.model.User;
import com.example.finance.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService service;

    @Test
    void createDelegatesToRepository() {
        Transaction transaction = new Transaction();
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = service.create(transaction);

        assertSame(transaction, result);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void getAllDelegatesToRepository() {
        User user = new User();
        user.setUsername("user@example.com");
        Transaction transaction = new Transaction();
        when(transactionRepository.findByUserAndDeletedFalseOrderByDateDesc(user)).thenReturn(List.of(transaction));

        List<Transaction> result = service.getAll(user);

        assertEquals(List.of(transaction), result);
        verify(transactionRepository).findByUserAndDeletedFalseOrderByDateDesc(user);
    }

    @Test
    void getBetweenDelegatesToRepository() {
        User user = new User();
        user.setUsername("user@example.com");
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        Transaction transaction = new Transaction();
        when(transactionRepository.findByUserAndDateBetweenAndDeletedFalseOrderByDateDesc(user, start, end)).thenReturn(List.of(transaction));

        List<Transaction> result = service.getBetween(user, start, end);

        assertEquals(List.of(transaction), result);
        verify(transactionRepository).findByUserAndDateBetweenAndDeletedFalseOrderByDateDesc(user, start, end);
    }
}
