package com.example.finance.controller;

import com.example.finance.model.User;
import com.example.finance.repository.UserRepository;
import com.example.finance.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerReturnsConflictWhenUsernameExists() {
        User user = new User();
        user.setUsername("user@example.com");
        when(userRepository.existsByUsername("user@example.com")).thenReturn(true);

        ResponseEntity<?> response = authController.register(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    void registerReturnsCreatedWithSavedUser() {
        User user = new User();
        user.setUsername("user@example.com");
        when(userRepository.existsByUsername("user@example.com")).thenReturn(false);
        when(userService.register(user)).thenReturn(user);

        ResponseEntity<?> response = authController.register(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(user, response.getBody());
        verify(userService).register(user);
    }

    @Test
    void loginAuthenticatesAndCreatesSession() {
        User requestUser = new User();
        requestUser.setUsername("user@example.com");
        requestUser.setPassword("secret");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(request.getSession(true)).thenReturn(null);

        ResponseEntity<?> response = authController.login(requestUser, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(request).getSession(true);
    }

    @Test
    void logoutDelegatesToServletLogout() throws Exception {
        doNothing().when(request).logout();

        ResponseEntity<?> response = authController.logout(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful", response.getBody());
        verify(request).logout();
    }
}
