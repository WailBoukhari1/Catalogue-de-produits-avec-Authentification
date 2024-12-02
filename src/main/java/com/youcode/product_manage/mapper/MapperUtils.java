package com.youcode.product_manage.mapper;

import java.util.List;

import org.springframework.data.domain.Page;

import com.youcode.product_manage.dto.response.PageResponse;

public class MapperUtils {
    
    public static <T> PageResponse<T> toPageResponse(Page<?> page, List<T> content) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(content);
        response.setPageNo(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;
    }
} 