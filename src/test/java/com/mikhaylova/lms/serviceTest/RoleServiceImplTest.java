package com.mikhaylova.lms.serviceTest;

import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.repository.RoleRepository;
import com.mikhaylova.lms.service.RoleService;
import com.mikhaylova.lms.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;
    private RoleService roleService;

    @BeforeEach
    void init(){
        roleService = new RoleServiceImpl(roleRepository);
    }

    @Test
    void findRoleByNameThrowsException(){
        Mockito.when(roleRepository.findRoleByName(anyString()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> roleService.findByName("BLA"));
    }
}
