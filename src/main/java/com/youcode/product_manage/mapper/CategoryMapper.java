package com.youcode.product_manage.mapper;

import com.youcode.product_manage.dto.request.CategoryRequest;
import com.youcode.product_manage.dto.response.CategoryResponse;
import com.youcode.product_manage.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    Category toEntity(CategoryRequest request);
    
    @Mapping(target = "productCount", expression = "java(category.getProducts().size())")
    CategoryResponse toResponse(Category category);
    
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}
