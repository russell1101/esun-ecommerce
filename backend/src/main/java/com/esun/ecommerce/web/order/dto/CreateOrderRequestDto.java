package com.esun.ecommerce.web.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDto {

    @NotEmpty(message = "訂單明細不能為空")
    @Valid
    private List<OrderItemDto> items;

    // 選填備註
    private String remark;
}
