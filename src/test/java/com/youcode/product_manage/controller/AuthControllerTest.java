package com.youcode.product_manage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youcode.product_manage.dto.request.LoginRequest;
import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.dto.response.UserResponse;
import com.youcode.product_manage.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void register_WithValidRequest_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        UserRequest request = new UserRequest();
        request.setLogin("testuser");
        request.setPassword("Test123!");
        request.setEmail("test@example.com");

        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(1L);
        mockResponse.setLogin("testuser");
        mockResponse.setEmail("test@example.com");
        mockResponse.setActive(true);

        when(userService.register(any(UserRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.login").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    // @Test
    // void register_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
    //     // Arrange
    //     UserRequest request = new UserRequest();
    //     request.setLogin(""); // Invalid login
    //     request.setPassword("weak"); // Invalid password
    //     request.setEmail("invalid-email"); // Invalid email

    //     // Act & Assert
    //     mockMvc.perform(post("/api/auth/register")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(request)))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.success").value(false));
    // }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccess() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setLogin("testuser");
        request.setPassword("Test123!");

        Authentication mockAuth = new UsernamePasswordAuthenticationToken("testuser", "Test123!");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mockAuth);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data").value("testuser"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setLogin("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void logout_ShouldReturnSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    @WithMockUser
    void checkAuthStatus_WhenAuthenticated_ShouldReturnAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Authenticated"));
    }

    // @Test
    // void checkAuthStatus_WhenNotAuthenticated_ShouldReturnNotAuthenticated() throws Exception {
    //     mockMvc.perform(get("/api/auth/check"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.success").value(false))
    //             .andExpect(jsonPath("$.message").value("Not authenticated"));
    // }
}