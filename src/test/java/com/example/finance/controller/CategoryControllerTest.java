package com.example.finance.controller;

import com.example.finance.model.Category;
import com.example.finance.model.CategoryType;
import com.example.finance.model.User;
import com.example.finance.repository.UserRepository;
import com.example.finance.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private CategoryController controller;

    @Test
    void getAllReturnsCategoriesForUser() {
        User user = new User();
        user.setUsername("user@example.com");
        Category category = new Category();
        category.setName("Groceries");
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryService.getAllForUser(user)).thenReturn(List.of(category));

        ResponseEntity<?> response = controller.getAll(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(category), response.getBody());
        verify(categoryService).getAllForUser(user);
    }

    @Test
    void createReturnsCreatedCategory() {
        User user = new User();
        user.setUsername("user@example.com");
        Category request = new Category();
        request.setName("Travel");
        request.setType(CategoryType.EXPENSE);
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryService.createCustom(request, user)).thenReturn(request);

        ResponseEntity<?> response = controller.create(request, principal);

        assertEquals(HttpStatus.valueOf(201), response.getStatusCode());
        assertEquals(request, response.getBody());
        verify(categoryService).createCustom(request, user);
    }

    @Test
    void deleteReturnsNotFoundWhenCategoryMissing() {
        User user = new User();
        user.setUsername("user@example.com");
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryService.getAllForUser(user)).thenReturn(List.of());

        ResponseEntity<?> response = controller.delete(7L, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Category not found", response.getBody());
    }

    @Test
    void deleteReturnsForbiddenForDifferentOwner() {
        User user = new User();
        user.setUsername("user@example.com");
        User other = new User();
        other.setUsername("other@example.com");
        Category category = new Category();
        category.setId(7L);
        category.setUser(other);
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryService.getAllForUser(user)).thenReturn(List.of(category));

        ResponseEntity<?> response = controller.delete(7L, principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Forbidden", response.getBody());
    }

    @Test
    void deleteReturnsOkWhenCategoryDeleted() {
        User user = new User();
        user.setUsername("user@example.com");
        Category category = new Category();
        category.setId(7L);
        category.setUser(user);
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryService.getAllForUser(user)).thenReturn(List.of(category));

        ResponseEntity<?> response = controller.delete(7L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category deleted successfully", response.getBody());
        verify(categoryService).deleteCategory(7L);
    }

    @Test
    void deleteReturnsBadRequestWhenServiceRejectsDeletion() {
        User user = new User();
        user.setUsername("user@example.com");
        Category category = new Category();
        category.setId(7L);
        category.setUser(user);
        when(principal.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryService.getAllForUser(user)).thenReturn(List.of(category));
        org.mockito.Mockito.doThrow(new IllegalStateException("Category is referenced by transactions")).when(categoryService).deleteCategory(7L);

        ResponseEntity<?> response = controller.delete(7L, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Category is referenced by transactions", response.getBody());
    }
}
