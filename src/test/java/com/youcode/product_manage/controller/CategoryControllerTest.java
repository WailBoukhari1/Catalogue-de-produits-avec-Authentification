package com.youcode.product_manage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youcode.product_manage.dto.request.CategoryRequest;
import com.youcode.product_manage.dto.response.CategoryResponse;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.service.CategoryService;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_WithValidRequest_ShouldReturnCreatedCategory() throws Exception {
        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("Test Category");
        request.setDescription("Test Description");

        CategoryResponse mockResponse = new CategoryResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Test Category");
        mockResponse.setDescription("Test Description");

        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Category created successfully"))
                .andExpect(jsonPath("$.data.name").value("Test Category"));
    }

    @Test
    @WithMockUser
    void getAllCategories_ShouldReturnPagedCategories() throws Exception {
        // Arrange
        PageResponse<CategoryResponse> mockPageResponse = new PageResponse<>();
        mockPageResponse.setContent(new ArrayList<>());
        mockPageResponse.setPageNo(0);
        mockPageResponse.setPageSize(10);
        mockPageResponse.setTotalElements(2);
        mockPageResponse.setTotalPages(1);
        mockPageResponse.setLast(true);

        when(categoryService.getAllCategories(any(Pageable.class))).thenReturn(mockPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/user/categories")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_WithValidId_ShouldReturnSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/admin/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

}