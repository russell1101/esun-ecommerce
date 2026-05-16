package com.esun.ecommerce.web.order.service;

import com.esun.ecommerce.web.order.dto.AdminOrderResponseDto;
import com.esun.ecommerce.web.order.dto.CreateOrderRequestDto;
import com.esun.ecommerce.web.order.dto.OrderDetailResponseDto;
import com.esun.ecommerce.web.order.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {

    String createOrder(CreateOrderRequestDto dto, Integer memberId);

    List<OrderResponseDto> getMyOrders(Integer memberId);

    List<AdminOrderResponseDto> getAllOrders();

    List<OrderDetailResponseDto> getOrderDetails(String orderId);
}
