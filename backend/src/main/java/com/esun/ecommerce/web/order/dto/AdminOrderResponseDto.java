package com.esun.ecommerce.web.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminOrderResponseDto {

    private String orderId;
    private Integer memberId;
    private String memberUsername;
    private BigDecimal totalPrice;
    private Byte payStatus;
    private Byte orderStatus;
    private String remark;
    private LocalDateTime inptime;
}
