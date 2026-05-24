package com.syfe.pfm.controller;

import com.syfe.pfm.dto.request.LoginRequest;
import com.syfe.pfm.dto.request.RegisterRequest;
import com.syfe.pfm.dto.response.MessageResponse;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<java.util.Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        var body = new java.util.HashMap<String, Object>();
        body.put("message", "User registered successfully");
        body.put("userId", user.getId());
        return ResponseEntity.status(201).body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(@Valid @RequestBody LoginRequest request, javax.servlet.http.HttpSession session) {
        var userOpt = userService.findByEmail(request.username());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new MessageResponse("Invalid credentials"));
        }
        var user = userOpt.get();
        if (!userService.checkPassword(user, request.password())) {
            return ResponseEntity.status(401).body(new MessageResponse("Invalid credentials"));
        }
        session.setAttribute("USER_ID", user.getId());
        return ResponseEntity.ok(new MessageResponse("Login successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(javax.servlet.http.HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }
}
