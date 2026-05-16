package com.esun.ecommerce.web.order.controller;

import com.esun.ecommerce.core.security.UserPrincipal;
import com.esun.ecommerce.core.util.ApiResponse;
import com.esun.ecommerce.web.order.dto.CreateOrderRequestDto;
import com.esun.ecommerce.web.order.dto.OrderResponseDto;
import com.esun.ecommerce.web.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/front/orders")
@RequiredArgsConstructor
public class FrontOrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<String> createOrder(
            @Valid @RequestBody CreateOrderRequestDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {
        String orderId = orderService.createOrder(dto, principal.getUserId());
        return ApiResponse.success(orderId);
    }

    @GetMapping("/me")
    public ApiResponse<List<OrderResponseDto>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(orderService.getMyOrders(principal.getUserId()));
    }
}
