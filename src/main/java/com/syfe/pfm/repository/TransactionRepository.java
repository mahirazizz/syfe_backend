package com.syfe.pfm.repository;

import java.time.LocalDate;
import java.util.List;

import com.syfe.pfm.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdAndDeletedFalseOrderByTransactionDateDesc(Long userId);

    List<Transaction> findByUserIdAndTransactionDateBetweenAndDeletedFalseOrderByTransactionDateDesc(Long userId, LocalDate start, LocalDate end);

    java.util.List<Transaction> findByUserIdAndCategoryIdAndDeletedFalseOrderByTransactionDateDesc(Long userId, Long categoryId);
    boolean existsByCategoryIdAndDeletedFalse(Long categoryId);
}
