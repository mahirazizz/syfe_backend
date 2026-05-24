package com.example.finance.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private LocalDate date;

    @ManyToOne
    private Category category;

    private String description;

    private boolean deleted = false;

    @ManyToOne
    private User user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
