package com.youcode.product_manage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.youcode.product_manage.dto.request.ProductRequest;
import com.youcode.product_manage.dto.response.ProductResponse;
import com.youcode.product_manage.entity.Product;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequest request);
    
    @Mapping(target = "category", source = "category")
    ProductResponse toResponse(Product product);
}
