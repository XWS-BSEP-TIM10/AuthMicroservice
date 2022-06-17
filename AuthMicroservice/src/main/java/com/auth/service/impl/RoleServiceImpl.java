package com.auth.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.model.Role;
import com.auth.repository.RoleRepository;
import com.auth.service.RoleService;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Override
  public Role findById(Long id) {
    Optional<Role> role = roleRepository.findById(id);
    if(role.isPresent())
      return role.get();
    return null;
  }

  @Override
  public Role findByName(String name) {
    return roleRepository.findByName(name);
  }


}
