package com.auth.service;

import java.util.List;


import com.auth.model.User;

public interface UserService {
    User findById(String id);
    User findByUsername(String username);
    List<User> findAll ();
    boolean userExists(String username);
    User save(User user);
    void delete(User user);
    User update(String id, String username);
}
