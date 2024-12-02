package com.youcode.product_manage.validation;

import org.springframework.stereotype.Component;

import com.youcode.product_manage.dto.request.CategoryRequest;
import com.youcode.product_manage.exception.ValidationException;
import com.youcode.product_manage.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryValidator {
    private final CategoryRepository categoryRepository;

    public void validateForCreation(CategoryRequest request) {
        validateName(request.getName());
        validateNameUniqueness(request.getName());
    }

    public void validateForUpdate(Long id, CategoryRequest request) {
        validateName(request.getName());
        validateNameUniquenessForUpdate(id, request.getName());
    }

    private void validateName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new ValidationException("Category name must be at least 3 characters");
        }
        if (name.trim().length() > 50) {
            throw new ValidationException("Category name must not exceed 50 characters");
        }
    }

    private void validateNameUniqueness(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ValidationException("Category with name '" + name + "' already exists");
        }
    }

    private void validateNameUniquenessForUpdate(Long id, String name) {
        categoryRepository.findByName(name).ifPresent(category -> {
            if (!category.getId().equals(id)) {
                throw new ValidationException("Category with name '" + name + "' already exists");
            }
        });
    }
}
