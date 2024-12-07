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
import com.youcode.product_manage.dto.request.ProductRequest;
import com.youcode.product_manage.dto.response.ProductResponse;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WithValidRequest_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setDesignation("Test Product");
        request.setPrice(99.99);
        request.setQuantity(10);
        request.setCategoryId(1L);

        ProductResponse mockResponse = ProductResponse.builder()
                .id(1L)
                .designation("Test Product")
                .price(99.99)
                .quantity(10)
                .build();

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andExpect(jsonPath("$.data.designation").value("Test Product"));
    }

    @Test
    @WithMockUser
    void getAllProducts_ShouldReturnPagedProducts() throws Exception {
        // Arrange
        PageResponse<ProductResponse> mockPageResponse = new PageResponse<>();
        mockPageResponse.setContent(new ArrayList<>());
        mockPageResponse.setPageNo(0);
        mockPageResponse.setPageSize(10);
        mockPageResponse.setTotalElements(2);
        mockPageResponse.setTotalPages(1);
        mockPageResponse.setLast(true);

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(mockPageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/user/products")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "designation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_WithValidId_ShouldReturnSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }

}