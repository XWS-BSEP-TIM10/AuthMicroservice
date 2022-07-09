package com.auth.service.impl;

import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    @Override
    public User findById(String id) throws AccessDeniedException {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }


    public List<User> findAll() throws AccessDeniedException {
        return userRepository.findAll();
    }

    @Override
    public boolean userExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    @Override
    public User saveOrRewrite(User user) {
        if (userRepository.findById(user.getId()).isPresent()) {
            return updateUser(user.getId(), user);
        }
        return userRepository.save(user);
    }

    private User updateUser(String id, User user) {
        User existingUser = findById(id);
        if (existingUser == null)
            return null;
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(existingUser);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User update(String id, String username) {
        User user = findById(id);
        if (user == null || findByUsername(username) != null)
            return null;
        user.setUsername(username);
        return userRepository.save(user);
    }

    @Override
    public String change2FAStatus(String userId, boolean enableFA) {
        User user = findById(userId);
        if (user == null)
            throw new RuntimeException();
        user.setUsing2FA(enableFA);
        userRepository.save(user);
        return enableFA ? user.getSecret() : "";
    }


}
