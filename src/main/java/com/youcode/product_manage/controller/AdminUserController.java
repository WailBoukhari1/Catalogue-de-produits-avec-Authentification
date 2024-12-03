package com.youcode.product_manage.controller;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.dto.response.UserResponse;
import com.youcode.product_manage.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(
            @PathVariable Long id, 
            @RequestBody Set<String> roles) {
        return ResponseEntity.ok(userService.updateUserRoles(id, roles));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 