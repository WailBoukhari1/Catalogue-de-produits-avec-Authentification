package com.youcode.product_manage.dto.response;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private int productCount;
} 