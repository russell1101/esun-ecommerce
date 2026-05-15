package com.esun.ecommerce.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer success;
    private String errMsg;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(1, null, data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(1, null, null);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
