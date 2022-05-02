package com.auth.service;

import java.util.List;

import com.auth.model.Role;

public interface RoleService {
	Role findById(Long id);
	Role findByName(String name);
}
