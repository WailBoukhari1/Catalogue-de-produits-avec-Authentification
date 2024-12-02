package com.youcode.product_manage.service.impl;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.product_manage.dto.request.ProductRequest;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.dto.response.ProductResponse;
import com.youcode.product_manage.entity.Product;
import com.youcode.product_manage.exception.ResourceNotFoundException;
import com.youcode.product_manage.mapper.MapperUtils;
import com.youcode.product_manage.mapper.ProductMapper;
import com.youcode.product_manage.repository.ProductRepository;
import com.youcode.product_manage.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return MapperUtils.toPageResponse(
            productPage,
            productPage.getContent().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ProductResponse> searchProducts(String designation, Pageable pageable) {
        Page<Product> productPage = productRepository.findByDesignationContainingIgnoreCase(designation, pageable);
        return MapperUtils.toPageResponse(
            productPage,
            productPage.getContent().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        return MapperUtils.toPageResponse(
            productPage,
            productPage.getContent().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(findProductById(id));
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductById(id);
        productMapper.updateEntityFromRequest(request, product);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public PageResponse<ProductResponse> searchProductsByCategory(String designation, Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByDesignationContainingIgnoreCaseAndCategoryId(designation, categoryId, pageable);
        return MapperUtils.toPageResponse(
            productPage,
            productPage.getContent().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
} 