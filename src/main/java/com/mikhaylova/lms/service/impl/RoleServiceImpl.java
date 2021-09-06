package com.mikhaylova.lms.service.impl;

import com.mikhaylova.lms.domain.Role;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.repository.RoleRepository;
import com.mikhaylova.lms.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findRoleByName(name)
                .orElseThrow(() -> new NotFoundException(Role.class.getSimpleName(), name));
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
