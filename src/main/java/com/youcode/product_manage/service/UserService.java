package com.youcode.product_manage.service;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.dto.response.UserResponse;

public interface UserService {
    PageResponse<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    UserResponse updateUserRoles(Long id, Set<String> roleNames);
}
