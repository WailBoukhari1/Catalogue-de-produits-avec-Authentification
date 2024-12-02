package com.youcode.product_manage.dto.response;

import java.util.Set;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String login;
    private String email;
    private boolean active;
    private Set<String> roles;
} 