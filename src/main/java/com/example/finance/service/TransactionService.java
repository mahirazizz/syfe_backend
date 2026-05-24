package com.example.finance.service;

import com.example.finance.model.Transaction;
import com.example.finance.model.User;
import com.example.finance.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction create(Transaction t) {
        return transactionRepository.save(t);
    }

    public List<Transaction> getAll(User user) {
        return transactionRepository.findByUserAndDeletedFalseOrderByDateDesc(user);
    }

    public List<Transaction> getBetween(User user, LocalDate start, LocalDate end) {
        return transactionRepository.findByUserAndDateBetweenAndDeletedFalseOrderByDateDesc(user, start, end);
    }
}
