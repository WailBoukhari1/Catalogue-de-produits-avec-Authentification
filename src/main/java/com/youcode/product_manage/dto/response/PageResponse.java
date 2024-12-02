package com.youcode.product_manage.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
} 