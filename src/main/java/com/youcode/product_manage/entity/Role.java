package com.youcode.product_manage.entity;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;
    
    public String getAuthority() {
        return name();
    }
}
