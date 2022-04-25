package com.auth.service;

import com.auth.dto.TokenDTO;

public interface AuthenticationService {
    TokenDTO login(String username, String password);
}
