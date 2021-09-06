package com.mikhaylova.lms.repositoryTest;

import com.mikhaylova.lms.domain.Role;
import com.mikhaylova.lms.repository.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Test
    void shouldFindRoleByName(){
        Optional<Role> actual = roleRepository.findRoleByName("ROLE_ADMIN");
        Assertions.assertEquals(2L, actual.get().getId());
    }

    @Test
    void shouldNotPresentWhenRoleDoesNotExist(){
        Optional<Role> actual = roleRepository.findRoleByName("ROLE_BLABLA");
        Assertions.assertFalse(actual.isPresent());
    }

}
