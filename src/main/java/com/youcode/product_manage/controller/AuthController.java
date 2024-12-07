package com.youcode.product_manage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

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
import com.youcode.product_manage.dto.response.AppApiResponse;
import com.youcode.product_manage.dto.response.UserResponse;
import com.youcode.product_manage.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully registered",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AppApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(AppApiResponse.success(
            "Registration successful", 
            userService.register(request)));
    }

    @Operation(summary = "Login user", description = "Authenticate user and create session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully logged in"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AppApiResponse<String>> login(
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
        
        return ResponseEntity.ok(AppApiResponse.success(
            "Login successful", 
            authentication.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<AppApiResponse<Void>> logout(HttpSession session) { 
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(AppApiResponse.success("Logout successful", null));
    }

    @PostMapping("/check")
    public ResponseEntity<AppApiResponse<String>> checkAuthStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return ResponseEntity.ok(AppApiResponse.success("Authenticated", auth.getName()));
        }
        return ResponseEntity.ok(AppApiResponse.error("Not authenticated"));
    }
}
