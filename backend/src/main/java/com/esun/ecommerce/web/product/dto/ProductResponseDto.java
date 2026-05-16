package com.esun.ecommerce.web.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseDto {

    private Integer productSn;
    private String productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private Byte status;
    private Integer version;
    private LocalDateTime inptime;
    private LocalDateTime updtime;
}
