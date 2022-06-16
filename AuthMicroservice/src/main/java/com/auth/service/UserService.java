package com.auth.service;

import com.auth.model.User;

import java.util.List;

public interface UserService {
    User findById(String id);

    User findByUsername(String username);

    List<User> findAll();

    boolean userExists(String username);

    User saveOrRewrite(User user);

    User save(User user);

    void delete(User user);

    User update(String id, String username);

    String change2FAStatus(String userId, boolean enableFA);
}
