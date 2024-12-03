package com.youcode.product_manage.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.dto.response.UserResponse;
import com.youcode.product_manage.entity.Role;
import com.youcode.product_manage.entity.User;
import com.youcode.product_manage.exception.ResourceNotFoundException;
import com.youcode.product_manage.exception.ValidationException;
import com.youcode.product_manage.mapper.MapperUtils;
import com.youcode.product_manage.mapper.UserMapper;
import com.youcode.product_manage.repository.UserRepository;
import com.youcode.product_manage.service.UserService;
import com.youcode.product_manage.validation.UserValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    @Override
    public UserResponse register(UserRequest request) {
        userValidator.validateRegistration(request);
        
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new ValidationException("Login already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.ROLE_ADMIN));
        
        return userMapper.toResponse(userRepository.save(user));
    }
    @Override
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return MapperUtils.toPageResponse(
            userPage,
            userPage.getContent().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    @Override
    public UserResponse updateUserRoles(Long id, Set<String> roleNames) {
        User user = findUserById(id);
        Set<Role> roles = roleNames.stream()
            .map(name -> {
                try {
                    return Role.valueOf(name);
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid role: " + name);
                }
            })
            .collect(Collectors.toSet());
            
        if (roles.isEmpty()) {
            throw new ValidationException("No valid roles provided");
        }
        
        user.setRoles(roles);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
} 