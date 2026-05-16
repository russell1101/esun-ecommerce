package com.esun.ecommerce.core.exception;

import com.esun.ecommerce.core.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕捉自定義業務異常
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBusinessException(BusinessException ex) {
        log.warn("商業邏輯錯誤: {}", ex.getMessage());
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    // 捕捉 Bean Validation 異常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("資料驗證失敗: {}", errorMessage);
        return ApiResponse.error(-2, "資料驗證失敗: " + errorMessage);
    }

    // 捕捉樂觀鎖異常
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<?> handleOptimisticLockException(ObjectOptimisticLockingFailureException ex) {
        log.warn("商品版本衝突（樂觀鎖）: {}", ex.getMessage());
        return ApiResponse.error(-1, "商品已搶先被修改，請重新整理後再試。");
    }

    // 捕捉其他所有未處理的異常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleAllUncaughtException(Exception ex) {
        log.error("發生未預期的錯誤", ex);
        return ApiResponse.error(-500, "系統發生未預期的錯誤，請聯繫管理員，或稍後再試");
    }
}
