package com.esun.ecommerce.web.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {

    private String orderId;
    private BigDecimal totalPrice;
    private Byte payStatus;
    private Byte orderStatus;
    private String remark;
    private LocalDateTime inptime;
    private List<OrderDetailResponseDto> details;
}
