package com.youcode.product_manage.validation;

import org.springframework.stereotype.Component;

import com.youcode.product_manage.dto.request.ProductRequest;
import com.youcode.product_manage.entity.Product;
import com.youcode.product_manage.exception.ValidationException;

@Component
public class ProductValidator {
    
    public void validateForUpdate(Product existingProduct, ProductRequest request) {
        validatePrice(request.getPrice());
        validateQuantity(request.getQuantity());
        validateDesignation(request.getDesignation());
    }
    
    public void validateForCreate(ProductRequest request) {
        validatePrice(request.getPrice());
        validateQuantity(request.getQuantity());
        validateDesignation(request.getDesignation());
    }
    
    private void validatePrice(Double price) {
        if (price > 1000000) {
            throw new ValidationException("Product price cannot exceed 1,000,000");
        }
    }
    
    private void validateQuantity(Integer quantity) {
        if (quantity > 10000) {
            throw new ValidationException("Product quantity cannot exceed 10,000 units");
        }
    }
    
    private void validateDesignation(String designation) {
        if (designation == null || designation.trim().isEmpty()) {
            throw new ValidationException("Product designation cannot be empty");
        }
        if (designation.length() < 2 || designation.length() > 100) {
            throw new ValidationException("Product designation must be between 2 and 100 characters");
        }
    }
}
