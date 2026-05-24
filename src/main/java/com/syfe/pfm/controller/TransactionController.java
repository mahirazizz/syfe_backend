package com.syfe.pfm.controller;

import java.util.List;

import com.syfe.pfm.dto.request.TransactionRequest;
import com.syfe.pfm.dto.request.UpdateTransactionRequest;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public com.syfe.pfm.dto.response.TransactionsResponse getAll(javax.servlet.http.HttpSession session,
                                                                 @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDate startDate,
                                                                 @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDate endDate,
                                                                 @org.springframework.web.bind.annotation.RequestParam(required = false) Long categoryId) {
        Long userId = (Long) session.getAttribute("USER_ID");
        java.util.List<Transaction> txns;
        if (startDate != null && endDate != null) {
            txns = transactionService.findByDateRangeForUser(userId, startDate, endDate);
        } else if (categoryId != null) {
            txns = transactionService.findByCategoryForUser(userId, categoryId);
        } else {
            txns = transactionService.findAllForUser(userId);
        }
        var resp = txns.stream().map(t -> new com.syfe.pfm.dto.response.TransactionResponse(t.getId(), t.getAmount(), t.getTransactionDate(), t.getCategory().getName(), t.getDescription(), t.getType())).toList();
        return new com.syfe.pfm.dto.response.TransactionsResponse(resp);
    }

    @PostMapping
    public ResponseEntity<com.syfe.pfm.dto.response.TransactionResponse> create(@Valid @RequestBody com.syfe.pfm.dto.request.TransactionRequest request, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        var t = transactionService.create(request, userId);
        var resp = new com.syfe.pfm.dto.response.TransactionResponse(t.getId(), t.getAmount(), t.getTransactionDate(), t.getCategory().getName(), t.getDescription(), t.getType());
        return ResponseEntity.status(201).body(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.syfe.pfm.dto.response.TransactionResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateTransactionRequest request, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        var t = transactionService.update(id, request, userId);
        var resp = new com.syfe.pfm.dto.response.TransactionResponse(t.getId(), t.getAmount(), t.getTransactionDate(), t.getCategory().getName(), t.getDescription(), t.getType());
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<com.syfe.pfm.dto.response.MessageResponse> delete(@PathVariable Long id, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        transactionService.delete(id, userId);
        return ResponseEntity.ok(new com.syfe.pfm.dto.response.MessageResponse("Transaction deleted successfully"));
    }
}
