package com.auth.service;


import com.auth.model.Role;

public interface RoleService {
	Role findById(Long id);
	Role findByName(String name);
}
