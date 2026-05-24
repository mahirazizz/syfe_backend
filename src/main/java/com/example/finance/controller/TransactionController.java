package com.example.finance.controller;

import com.example.finance.model.Category;
import com.example.finance.model.Transaction;
import com.example.finance.model.User;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.TransactionRepository;
import com.example.finance.repository.UserRepository;
import com.example.finance.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository, CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Transaction request, Principal principal) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be positive");
        }
        if (request.getDate() == null || request.getDate().isAfter(LocalDate.now())) {
            return ResponseEntity.badRequest().body("Invalid date");
        }
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        Category cat = categoryRepository.findById(request.getCategory().getId()).orElse(null);
        if (cat == null) return ResponseEntity.badRequest().body("Invalid category");
        request.setCategory(cat);
        request.setUser(user);
        Transaction saved = transactionService.create(request);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        List<Transaction> list;
        if (startDate != null && endDate != null) {
            list = transactionService.getBetween(user, startDate, endDate);
        } else {
            list = transactionService.getAll(user);
        }
        return ResponseEntity.ok().body(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Transaction request, Principal principal) {
        Transaction tx = transactionRepository.findById(id).orElse(null);
        if (tx == null || tx.isDeleted()) return ResponseEntity.notFound().build();
        if (!tx.getUser().getUsername().equals(principal.getName())) return ResponseEntity.status(403).build();
        if (request.getAmount() != null) tx.setAmount(request.getAmount());
        if (request.getDescription() != null) tx.setDescription(request.getDescription());
        // date not modifiable
        transactionRepository.save(tx);
        return ResponseEntity.ok().body(tx);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Principal principal) {
        Transaction tx = transactionRepository.findById(id).orElse(null);
        if (tx == null) return ResponseEntity.notFound().build();
        if (!tx.getUser().getUsername().equals(principal.getName())) return ResponseEntity.status(403).build();
        tx.setDeleted(true);
        transactionRepository.save(tx);
        return ResponseEntity.ok().body("Transaction deleted successfully");
    }
}
