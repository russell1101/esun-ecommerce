package com.esun.ecommerce.web.product.controller;

import com.esun.ecommerce.core.util.ApiResponse;
import com.esun.ecommerce.web.product.dto.ProductResponseDto;
import com.esun.ecommerce.web.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/front/products")
@RequiredArgsConstructor
public class FrontProductController {

    private final ProductService productService;

    // 公開API：查詢上架且有庫存的商品清單
    @GetMapping("/available")
    public ApiResponse<List<ProductResponseDto>> getAvailable() {
        return ApiResponse.success(productService.getAvailableProducts());
    }
}
