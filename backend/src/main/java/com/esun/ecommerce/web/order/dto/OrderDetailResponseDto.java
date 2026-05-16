package com.esun.ecommerce.web.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailResponseDto {

    private Integer orderItemSn;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal standPrice;
    private BigDecimal itemPrice;
}
