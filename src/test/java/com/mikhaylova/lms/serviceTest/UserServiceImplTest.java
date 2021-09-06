package com.mikhaylova.lms.serviceTest;

import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.UserDto;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.mapper.UserMapper;
import com.mikhaylova.lms.repository.UserRepository;
import com.mikhaylova.lms.service.SecurityService;
import com.mikhaylova.lms.service.UserService;
import com.mikhaylova.lms.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private SecurityService securityService;
    private UserService userService;

    @BeforeEach
    void init() {
        userService = new UserServiceImpl(userRepository, userMapper, securityService);
    }

    @Test
    void getUserByIdThrowsException() {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void findUserByIdThrowsException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void findUserByUsernameThrowsException() {
        Mockito.when(userRepository.findUserByUsername(anyString()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserByUsername("BLA"));
    }

    @Test
    void findUserDtoByIdException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserDtoById(anyLong()));
    }

    @Test
    void testSaveUserDtoWhenUserChangedHimself() {
        UserService spyUserService = spy(userService);
        doReturn(new User(1L, "test", new HashSet<>(), new HashSet<>(), "qwerty"))
                .when(spyUserService)
                .getUserById(anyLong());
        Mockito.when(securityService.getCurrentPrincipalUsername()).thenReturn("test");
        spyUserService.saveUserDto(new UserDto(1L, "test"));
        verify(securityService, times(1)).changeAuthNameAndPassword(any(User.class));
    }

    @Test
    void testSaveUserDtoWhenUserChangedNotHimself() {
        UserService spyUserService = spy(userService);
        doReturn(new User(1L, "test", new HashSet<>(), new HashSet<>(), "qwerty"))
                .when(spyUserService)
                .getUserById(anyLong());
        Mockito.when(securityService.getCurrentPrincipalUsername()).thenReturn("test1");
        spyUserService.saveUserDto(new UserDto(1L, "test"));
        verify(securityService, times(0)).changeAuthNameAndPassword(any(User.class));
    }

}
