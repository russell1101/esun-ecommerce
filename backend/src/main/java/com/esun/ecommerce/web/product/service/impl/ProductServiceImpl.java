package com.esun.ecommerce.web.product.service.impl;

import com.esun.ecommerce.core.annotation.DbOperation;
import com.esun.ecommerce.core.entity.Product;
import com.esun.ecommerce.core.exception.BusinessException;
import com.esun.ecommerce.web.product.dto.ProductRequestDto;
import com.esun.ecommerce.web.product.dto.ProductResponseDto;
import com.esun.ecommerce.web.product.repository.ProductRepository;
import com.esun.ecommerce.web.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    @DbOperation(action = "新增商品")
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        String productId = generateProductId();

        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : (byte) 1);

        return toDto(productRepository.save(product));
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @DbOperation(action = "更新商品")
    public ProductResponseDto updateProduct(String productId, ProductRequestDto dto) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException("商品不存在"));

        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }

        return toDto(productRepository.save(product));
    }

    @Override
    public List<ProductResponseDto> getAvailableProducts() {
        return productRepository
                .findByStatusAndQuantityGreaterThanOrderByInptimeDesc((byte) 1, 0)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 產生商品編號：P + 毫秒後9碼
     */
    private String generateProductId() {
        return "P" + String.format("%09d", System.currentTimeMillis() % 1_000_000_000L);
    }

    private ProductResponseDto toDto(Product p) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setProductSn(p.getProductSn());
        dto.setProductId(p.getProductId());
        dto.setProductName(p.getProductName());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantity());
        dto.setStatus(p.getStatus());
        dto.setVersion(p.getVersion());
        dto.setInptime(p.getInptime());
        dto.setUpdtime(p.getUpdtime());
        return dto;
    }
}
