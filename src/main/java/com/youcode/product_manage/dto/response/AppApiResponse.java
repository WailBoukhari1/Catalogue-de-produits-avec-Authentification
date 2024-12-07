package com.youcode.product_manage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> AppApiResponse<T> success(T data) {
        return new AppApiResponse<>(true, "Operation successful", data);
    }
    
    public static <T> AppApiResponse<T> success(String message, T data) {
        return new AppApiResponse<>(true, message, data);
    }
    
    public static <T> AppApiResponse<T> error(String message) {
        return new AppApiResponse<>(false, message, null);
    }
    
    public static <T> AppApiResponse<T> error(String message, T data) {
        return new AppApiResponse<>(false, message, data);
    }
} 