package com.esun.ecommerce.web.product.service;

import com.esun.ecommerce.web.product.dto.ProductRequestDto;
import com.esun.ecommerce.web.product.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto dto);

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto updateProduct(String productId, ProductRequestDto dto);

    List<ProductResponseDto> getAvailableProducts();
}
