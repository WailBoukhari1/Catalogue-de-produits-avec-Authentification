package com.youcode.product_manage.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    private String login;
    
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$", 
        message = "Password must be at least 8 characters and contain at least one digit, one lowercase and one uppercase letter")
    private String password;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
} 