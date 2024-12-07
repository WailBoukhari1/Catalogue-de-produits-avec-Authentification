package com.youcode.product_manage.dto.response;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private int productCount;

    // Default constructor
    public CategoryResponse() {}

    // Constructor with parameters
    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
} 