package com.example.finance.service;

import com.example.finance.model.User;
import com.example.finance.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    void registerEncodesPasswordAndSavesUser() {
        User user = new User();
        user.setUsername("user@example.com");
        user.setPassword("plain-text");
        when(passwordEncoder.encode("plain-text")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);

        User result = service.register(user);

        assertSame(user, result);
        assertEquals("encoded-password", user.getPassword());
        verify(passwordEncoder).encode("plain-text");
        verify(userRepository).save(user);
    }
}
