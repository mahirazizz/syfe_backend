package com.syfe.pfm.repository;

import java.util.Optional;

import com.syfe.pfm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
