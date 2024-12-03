package com.youcode.product_manage.mapper;

import com.youcode.product_manage.dto.request.ProductRequest;
import com.youcode.product_manage.dto.response.ProductResponse;
import com.youcode.product_manage.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {
    
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest request);
    
    ProductResponse toResponse(Product product);
    
    @Mapping(target = "category.id", source = "categoryId")
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);
}
