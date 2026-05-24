package com.example.finance.controller;

import com.example.finance.model.User;
import com.example.finance.repository.UserRepository;
import com.example.finance.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        User saved = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request, HttpServletRequest httpRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        // ensure session is created so the session cookie is sent
        httpRequest.getSession(true);
        return ResponseEntity.ok().body("Login successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) throws Exception {
        request.logout();
        return ResponseEntity.ok().body("Logout successful");
    }
}
