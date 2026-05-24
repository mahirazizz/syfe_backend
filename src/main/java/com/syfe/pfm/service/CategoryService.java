package com.syfe.pfm.service;

import java.util.List;

import com.syfe.pfm.dto.request.CategoryRequest;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.exception.ResourceNotFoundException;
import com.syfe.pfm.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final com.syfe.pfm.repository.TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository, com.syfe.pfm.repository.TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public Category create(CategoryRequest request) {
        throw new UnsupportedOperationException("Use createForUser for custom categories");
    }

    public Category createForUser(CategoryRequest request, Long userId) {
        // ensure uniqueness per user
        if (categoryRepository.findByNameAndUserId(request.name(), userId).isPresent()) {
            throw new com.syfe.pfm.exception.BadRequestException("Category name already exists for user");
        }
        Category category = new Category();
        category.setName(request.name());
        category.setType(request.type());
        category.setCustom(true);
        category.setUser(new com.syfe.pfm.entity.User());
        category.getUser().setId(userId);
        return categoryRepository.save(category);
    }

    public java.util.Optional<Category> findByNameForUserOrDefault(String name, Long userId) {
        var byUser = categoryRepository.findByNameAndUserId(name, userId);
        if (byUser.isPresent()) return byUser;
        return categoryRepository.findByNameAndCustomFalse(name);
    }

    public void deleteByNameForUser(String name, Long userId) {
        var catOpt = categoryRepository.findByNameAndUserId(name, userId);
        if (catOpt.isEmpty()) {
            throw new com.syfe.pfm.exception.ResourceNotFoundException("Category not found");
        }
        Category category = catOpt.get();
        if (transactionRepository.existsByCategoryIdAndDeletedFalse(category.getId())) {
            throw new com.syfe.pfm.exception.BadRequestException("Category is referenced by transactions and cannot be deleted");
        }
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<Category> findAllForUser(Long userId) {
        // Return default (non-custom) categories plus this user's custom categories
        List<Category> all = categoryRepository.findAll();
        return all.stream()
                .filter(c -> !Boolean.TRUE.equals(c.isCustom()) || (c.getUser() != null && c.getUser().getId() != null && c.getUser().getId().equals(userId)))
                .toList();
    }

    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
