package com.example.finance.repository;

import com.example.finance.model.Category;
import com.example.finance.model.Transaction;
import com.example.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserAndDeletedFalseOrderByDateDesc(User user);

    List<Transaction> findByUserAndDateBetweenAndDeletedFalseOrderByDateDesc(User user, LocalDate start, LocalDate end);

    boolean existsByCategoryAndDeletedFalse(Category category);
}
