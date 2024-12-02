package com.youcode.product_manage.service.impl;

import com.youcode.product_manage.dto.request.CategoryRequest;
import com.youcode.product_manage.dto.response.CategoryResponse;
import com.youcode.product_manage.dto.response.PageResponse;
import com.youcode.product_manage.entity.Category;
import com.youcode.product_manage.exception.ResourceNotFoundException;
import com.youcode.product_manage.mapper.CategoryMapper;
import com.youcode.product_manage.mapper.MapperUtils;
import com.youcode.product_manage.repository.CategoryRepository;
import com.youcode.product_manage.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return MapperUtils.toPageResponse(
            categoryPage,
            categoryPage.getContent().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    @Override
    public PageResponse<CategoryResponse> searchCategories(String name, Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        return MapperUtils.toPageResponse(
            categoryPage,
            categoryPage.getContent().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        return categoryMapper.toResponse(findCategoryById(id));
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryById(id);
        categoryMapper.updateEntityFromRequest(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}