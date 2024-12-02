package com.youcode.product_manage.validation;

import org.springframework.stereotype.Component;

import com.youcode.product_manage.dto.request.ProductRequest;
import com.youcode.product_manage.exception.ValidationException;
import com.youcode.product_manage.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final CategoryRepository categoryRepository;

    public void validateForCreation(ProductRequest request) {
        validateDesignation(request.getDesignation());
        validatePrice(request.getPrice());
        validateQuantity(request.getQuantity());
        validateCategory(request.getCategoryId());
    }

    public void validateForUpdate(ProductRequest request) {
        validateDesignation(request.getDesignation());
        validatePrice(request.getPrice());
        validateQuantity(request.getQuantity());
        validateCategory(request.getCategoryId());
    }

    private void validateDesignation(String designation) {
        if (designation == null || designation.trim().length() < 3) {
            throw new ValidationException("Product designation must be at least 3 characters");
        }
        if (designation.trim().length() > 100) {
            throw new ValidationException("Product designation must not exceed 100 characters");
        }
    }

    private void validatePrice(Double price) {
        if (price == null || price <= 0) {
            throw new ValidationException("Product price must be greater than 0");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new ValidationException("Product quantity cannot be negative");
        }
    }

    private void validateCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ValidationException("Invalid category ID: " + categoryId);
        }
    }
}
