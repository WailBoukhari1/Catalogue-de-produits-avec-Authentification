package com.youcode.product_manage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youcode.product_manage.dto.request.LoginRequest;
import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.dto.response.ApiResponse;
import com.youcode.product_manage.dto.response.UserResponse;
import com.youcode.product_manage.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            "Registration successful", 
            userService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getLogin(), 
                request.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        
        return ResponseEntity.ok(ApiResponse.success(
            "Login successful", 
            authentication.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @PostMapping("/check")
    public ResponseEntity<ApiResponse<String>> checkAuthStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return ResponseEntity.ok(ApiResponse.success("Authenticated", auth.getName()));
        }
        return ResponseEntity.ok(ApiResponse.error("Not authenticated"));
    }
}
