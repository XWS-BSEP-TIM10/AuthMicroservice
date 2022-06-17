package com.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}

