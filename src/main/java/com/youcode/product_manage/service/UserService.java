package com.youcode.product_manage.service;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.dto.response.UserResponse;

public interface UserService {
    UserResponse register(UserRequest request);
    PageResponse<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUserRoles(Long id, Set<String> roles);
    void deleteUser(Long id);
}
