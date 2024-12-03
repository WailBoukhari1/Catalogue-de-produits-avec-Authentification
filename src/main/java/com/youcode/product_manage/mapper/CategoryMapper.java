package com.youcode.product_manage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.youcode.product_manage.dto.request.CategoryRequest;
import com.youcode.product_manage.dto.response.CategoryResponse;
import com.youcode.product_manage.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequest request);
    
    @Mapping(target = "productCount", expression = "java(category.getProducts() != null ? category.getProducts().size() : 0)")
    CategoryResponse toResponse(Category category);
    
    @Mapping(target = "products", ignore = true)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}
