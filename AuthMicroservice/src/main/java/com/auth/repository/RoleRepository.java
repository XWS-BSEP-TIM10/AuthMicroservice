package com.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
	Role findById(String id);
}
