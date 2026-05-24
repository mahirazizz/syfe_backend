package com.syfe.pfm.service;

import java.util.List;

import com.syfe.pfm.dto.request.TransactionRequest;
import com.syfe.pfm.dto.request.UpdateTransactionRequest;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransactionService {

    private final com.syfe.pfm.repository.TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public TransactionService(com.syfe.pfm.repository.TransactionRepository transactionRepository,
                              UserService userService,
                              CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public Transaction create(TransactionRequest request, Long userId) {
        if (request.date().isAfter(java.time.LocalDate.now())) {
            throw new com.syfe.pfm.exception.BadRequestException("Transaction date cannot be in the future");
        }
        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setType(request.type());
        transaction.setTransactionDate(request.date());
        // resolve category by name: user custom first, then default non-custom
        var catOpt = categoryService.findByNameForUserOrDefault(request.category(), userId);
        if (catOpt.isEmpty()) throw new com.syfe.pfm.exception.BadRequestException("Category not found");
        com.syfe.pfm.entity.Category category = catOpt.get();
        transaction.setCategory(category);
        transaction.setUser(userService.getById(userId));
        transaction.setDeleted(false);
        return transactionRepository.save(transaction);
    }

    public Transaction update(Long id, UpdateTransactionRequest request, Long userId) {
        Transaction transaction = getById(id);
        if (!transaction.getUser().getId().equals(userId)) {
            throw new com.syfe.pfm.exception.UnauthorizedException("Not allowed");
        }
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAllForUser(Long userId) {
        return transactionRepository.findByUserIdAndDeletedFalseOrderByTransactionDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByDateRangeForUser(Long userId, java.time.LocalDate start, java.time.LocalDate end) {
        return transactionRepository.findByUserIdAndTransactionDateBetweenAndDeletedFalseOrderByTransactionDateDesc(userId, start, end);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByCategoryForUser(Long userId, Long categoryId) {
        return transactionRepository.findByUserIdAndCategoryIdAndDeletedFalseOrderByTransactionDateDesc(userId, categoryId);
    }

    @Transactional(readOnly = true)
    public Transaction getById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    public void delete(Long id, Long userId) {
        Transaction tx = getById(id);
        if (!tx.getUser().getId().equals(userId)) {
            throw new com.syfe.pfm.exception.UnauthorizedException("Not allowed");
        }
        tx.setDeleted(true);
        transactionRepository.save(tx);
    }
}
