package com.esun.ecommerce.web.order.controller;

import com.esun.ecommerce.core.util.ApiResponse;
import com.esun.ecommerce.web.order.dto.AdminOrderResponseDto;
import com.esun.ecommerce.web.order.dto.OrderDetailResponseDto;
import com.esun.ecommerce.web.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    // 查詢所有訂單主檔
    @GetMapping
    public ApiResponse<List<AdminOrderResponseDto>> getAllOrders() {
        return ApiResponse.success(orderService.getAllOrders());
    }

    // 查詢指定訂單明細
    @GetMapping("/{orderId}/details")
    public ApiResponse<List<OrderDetailResponseDto>> getOrderDetails(@PathVariable String orderId) {
        return ApiResponse.success(orderService.getOrderDetails(orderId));
    }
}
