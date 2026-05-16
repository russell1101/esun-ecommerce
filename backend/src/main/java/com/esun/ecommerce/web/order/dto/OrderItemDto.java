package com.esun.ecommerce.web.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemDto {

    @NotBlank(message = "商品編號不能為空")
    private String productId;

    @NotNull(message = "購買數量不能為空")
    @Min(value = 1, message = "購買數量至少為 1")
    private Integer quantity;
}
