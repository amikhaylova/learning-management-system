package com.mikhaylova.lms.service.impl;

import com.mikhaylova.lms.domain.User;
import com.mikhaylova.lms.dto.RegisterUserDto;
import com.mikhaylova.lms.dto.UserDto;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.mapper.UserMapper;
import com.mikhaylova.lms.repository.UserRepository;
import com.mikhaylova.lms.service.SecurityService;
import com.mikhaylova.lms.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private SecurityService securityService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, SecurityService securityService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.securityService = securityService;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                .collect(Collectors.toList())
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User getUserById(Long id) {
        if (existsById(id))
            return userRepository.getById(id);
        else
            throw new NotFoundException(User.class.getSimpleName(), id);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), id));
    }

    @Override
    public List<User> findUsersNotAssignedToCourse(Long CourseId) {
        return userRepository.findUsersNotAssignedToCourse(CourseId);
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public void saveRegisterUserDto(RegisterUserDto registerUserDto) {
        userRepository.save(userMapper.mapRegisterUserDtoToUser(registerUserDto));
    }

    @Override
    public void saveUserDto(UserDto userDto) {
        User user = getUserById(userDto.getId());
        String principalName = securityService.getCurrentPrincipalUsername();
        boolean isUserChangingHimself = user.getUsername().equals(principalName);
        user.setModificationDate(new Date());
        user.setModificationAuthor(findUserByUsername(principalName));
        userMapper.mapUserDtoToUser(userDto, user);
        userRepository.save(user);
        if (isUserChangingHimself)
            securityService.changeAuthNameAndPassword(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), username));
    }

    @Override
    public UserDto findUserDtoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), id));
        return userMapper.mapUserToUserDto(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<UserDto> findAllUserDto() {
        return userRepository.findAllUserDto();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
