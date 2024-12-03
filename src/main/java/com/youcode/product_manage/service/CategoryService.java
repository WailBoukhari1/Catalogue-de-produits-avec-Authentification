package com.youcode.product_manage.service;

import org.springframework.data.domain.Pageable;

import com.youcode.product_manage.dto.request.CategoryRequest;
import com.youcode.product_manage.dto.response.CategoryResponse;
import com.youcode.product_manage.dto.response.PageResponse;

public interface CategoryService {
    // User operations
    PageResponse<CategoryResponse> getAllCategories(Pageable pageable);
    PageResponse<CategoryResponse> searchCategories(String name, Pageable pageable);
    
    // Admin operations
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}
