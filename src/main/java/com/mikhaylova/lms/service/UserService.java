package com.mikhaylova.lms.service;

import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.RegisterUserDto;
import com.mikhaylova.lms.dto.UserDto;

import java.util.List;

public interface UserService {
    User getUserById(Long id);

    User findUserById(Long id);

    List<User> findUsersNotAssignedToCourse(Long CourseId);

    boolean existsById(Long userId);

    void saveRegisterUserDto(RegisterUserDto registerUserDto);

    void saveUserDto(UserDto userDto);

    User findUserByUsername(String username);

    UserDto findUserDtoById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserDto> findAllUserDto();
}
