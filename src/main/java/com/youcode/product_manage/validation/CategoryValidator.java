package com.youcode.product_manage.validation;

import org.springframework.stereotype.Component;

import com.youcode.product_manage.dto.request.CategoryRequest;
import com.youcode.product_manage.entity.Category;
import com.youcode.product_manage.exception.InvalidOperationException;
import com.youcode.product_manage.exception.ValidationException;

@Component
public class CategoryValidator {
    
    public void validateForUpdate(Category existingCategory, CategoryRequest request) {
        validateName(request.getName());
        validateDescription(request.getDescription());
    }
    
    public void validateForCreate(CategoryRequest request) {
        validateName(request.getName());
        validateDescription(request.getDescription());
    }
    
    public void validateForDelete(Category category) {
        if (!category.getProducts().isEmpty()) {
            throw new InvalidOperationException("Cannot delete category with existing products");
        }
    }
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty");
        }
        if (name.length() < 2 || name.length() > 50) {
            throw new ValidationException("Category name must be between 2 and 50 characters");
        }
    }
    
    private void validateDescription(String description) {
        if (description != null && description.length() > 255) {
            throw new ValidationException("Category description cannot exceed 255 characters");
        }
    }
}
