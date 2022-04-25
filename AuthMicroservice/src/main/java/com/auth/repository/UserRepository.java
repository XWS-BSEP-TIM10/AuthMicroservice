package com.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

