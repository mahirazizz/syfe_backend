package com.syfe.pfm.controller;

import java.util.List;

import com.syfe.pfm.dto.request.CategoryRequest;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public java.util.Map<String, Object> getAll(javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        var cats = categoryService.findAllForUser(userId).stream()
                .map(c -> new com.syfe.pfm.dto.response.CategoryResponse(c.getName(), c.getType(), Boolean.TRUE.equals(c.isCustom())))
                .toList();
        var body = new java.util.HashMap<String, Object>();
        body.put("categories", cats);
        return body;
    }

    @PostMapping
    public ResponseEntity<com.syfe.pfm.dto.response.CategoryResponse> create(@Valid @RequestBody CategoryRequest request, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        var c = categoryService.createForUser(request, userId);
        var resp = new com.syfe.pfm.dto.response.CategoryResponse(c.getName(), c.getType(), true);
        return ResponseEntity.status(201).body(resp);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<com.syfe.pfm.dto.response.MessageResponse> delete(@PathVariable String name, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        categoryService.deleteByNameForUser(name, userId);
        return ResponseEntity.ok(new com.syfe.pfm.dto.response.MessageResponse("Category deleted successfully"));
    }
}
