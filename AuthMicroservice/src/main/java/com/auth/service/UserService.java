package com.auth.service;

import java.util.List;


import com.auth.model.User;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll ();
    boolean userExists(String username);
}
