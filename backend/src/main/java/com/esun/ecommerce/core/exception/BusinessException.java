package com.esun.ecommerce.core.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = -1; // 預設為一般業務錯誤
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
