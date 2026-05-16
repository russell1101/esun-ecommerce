package com.esun.ecommerce.web.product.controller;

import com.esun.ecommerce.core.util.ApiResponse;
import com.esun.ecommerce.web.product.dto.ProductRequestDto;
import com.esun.ecommerce.web.product.dto.ProductResponseDto;
import com.esun.ecommerce.web.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto dto) {
        return ApiResponse.success(productService.createProduct(dto));
    }

    @GetMapping
    public ApiResponse<List<ProductResponseDto>> getAll() {
        return ApiResponse.success(productService.getAllProducts());
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductResponseDto> update(
            @PathVariable String productId,
            @Valid @RequestBody ProductRequestDto dto) {
        return ApiResponse.success(productService.updateProduct(productId, dto));
    }
}
