package com.youcode.product_manage.dto.response;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String designation;
    private Double price;
    private Integer quantity;
    // private CategoryResponse category;
} 