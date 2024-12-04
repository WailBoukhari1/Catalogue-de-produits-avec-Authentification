package com.youcode.product_manage.service.impl;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youcode.product_manage.dto.request.ProductRequest;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.dto.response.ProductResponse;
import com.youcode.product_manage.entity.Category;
import com.youcode.product_manage.entity.Product;
import com.youcode.product_manage.exception.DuplicateResourceException;
import com.youcode.product_manage.exception.ResourceNotFoundException;
import com.youcode.product_manage.mapper.MapperUtils;
import com.youcode.product_manage.mapper.ProductMapper;
import com.youcode.product_manage.repository.CategoryRepository;
import com.youcode.product_manage.repository.ProductRepository;
import com.youcode.product_manage.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

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
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        
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
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        if (productRepository.existsByDesignation(request.getDesignation())) {
            throw new DuplicateResourceException("Product with designation '" + request.getDesignation() + "' already exists");
        }
        
        Product product = Product.builder()
            .designation(request.getDesignation())
            .price(request.getPrice())
            .quantity(request.getQuantity())
            .category(category)
            .build();
        
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = findProductById(id);
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        if (!existingProduct.getDesignation().equals(request.getDesignation()) 
                && productRepository.existsByDesignation(request.getDesignation())) {
            throw new DuplicateResourceException("Product with designation '" + request.getDesignation() + "' already exists");
        }
        
        existingProduct.setDesignation(request.getDesignation());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());
        existingProduct.setCategory(category);
        
        return productMapper.toResponse(productRepository.save(existingProduct));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
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
