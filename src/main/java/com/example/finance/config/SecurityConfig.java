package com.example.finance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/h2-console/**").permitAll()
                .anyRequest().authenticated())
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .logout(logout -> logout.logoutUrl("/api/auth/logout").permitAll());

        // allow H2 console frames
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}
