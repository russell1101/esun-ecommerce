package com.esun.ecommerce.web.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDto {

    @NotBlank(message = "商品名稱不能為空")
    private String productName;

    @NotNull(message = "售價不能為空")
    @DecimalMin(value = "0.01", message = "售價必須大於 0")
    private BigDecimal price;

    @NotNull(message = "庫存不能為空")
    @Min(value = 0, message = "庫存不能為負數")
    private Integer quantity;

    //（預設 1 上架）
    private Byte status;
}
