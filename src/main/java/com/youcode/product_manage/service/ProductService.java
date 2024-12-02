package com.youcode.product_manage.service;

import org.springframework.data.domain.Pageable;

import com.youcode.product_manage.dto.request.ProductRequest;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.dto.response.ProductResponse;

public interface ProductService {
    PageResponse<ProductResponse> getAllProducts(Pageable pageable);
    PageResponse<ProductResponse> searchProducts(String designation, Pageable pageable);
    PageResponse<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
    PageResponse<ProductResponse> searchProductsByCategory(String designation, Long categoryId, Pageable pageable);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
