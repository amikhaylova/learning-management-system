package com.mikhaylova.lms.repositoryTest;

import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.UserDto;
import com.mikhaylova.lms.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void shouldFindUsersNotAssignedToCourse(){
        List<User> actual = userRepository.findUsersNotAssignedToCourse(1L);
        Set<Long> expect = new HashSet<>();
        for (int i = 1; i < 33; i++){
            if (i != 8 && i != 13 && i != 15 &&i != 24){
                expect.add(Long.valueOf(i));
            }
        }
        Assertions.assertEquals(expect.size(), actual.size());
        Set<Long> actualSet = actual.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expect, actualSet);
    }

    @Test
    void shouldReturnFullListWhenCourseDoesNotExist(){
        List<User> actual = userRepository.findUsersNotAssignedToCourse(16L);
        Assertions.assertEquals(32, actual.size());
    }

    @Test
    void shouldReturnUserByUsername(){
        Optional<User> actual = userRepository.findUserByUsername("Louise Caveney");
        Assertions.assertEquals(1L, actual.get().getId());

    }


    @Test
    void shouldNotPresentWhenUsernameDoesNotExist(){
        Optional<User> actual = userRepository.findUserByUsername("blabla");
        Assertions.assertFalse(actual.isPresent());

    }

    @Test
    void shouldReturnTrueWhenUsernameExists(){
        boolean actual = userRepository.existsByUsername("Louise Caveney");
        Assertions.assertTrue(actual);
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist(){
        boolean actual = userRepository.existsByUsername("blabla");
        Assertions.assertFalse(actual);
    }

    @Test
    void shouldReturnAllUserDto(){
        List<UserDto> actual = userRepository.findAllUserDto();
        Assertions.assertEquals(32, actual.size());
    }


}
