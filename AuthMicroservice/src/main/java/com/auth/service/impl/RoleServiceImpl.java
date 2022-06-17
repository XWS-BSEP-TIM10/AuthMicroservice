package com.auth.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.model.Role;
import com.auth.repository.RoleRepository;
import com.auth.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Override
  public Role findById(Long id) {
    return this.roleRepository.findById(id).get();
  }

  @Override
  public Role findByName(String name) {
    return roleRepository.findByName(name);
  }


}
