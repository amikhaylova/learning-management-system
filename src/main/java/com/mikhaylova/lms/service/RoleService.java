package com.mikhaylova.lms.service;

import com.mikhaylova.lms.domain.Role;

import java.util.List;

public interface RoleService {
    Role findByName(String name);

    List<Role> findAll();
}
