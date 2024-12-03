package com.youcode.product_manage.validation;

import org.springframework.stereotype.Component;

import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.exception.ValidationException;

@Component
public class UserValidator {
    
    public void validateRegistration(UserRequest request) {
        if (request == null) {
            throw new ValidationException("User request cannot be null");
        }
        
        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        
        if (request.getPassword() == null || !request.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
            throw new ValidationException("Password must be at least 8 characters and contain at least one digit, lowercase and uppercase letter");
        }
        
        if (request.getEmail() == null || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
    }
}
