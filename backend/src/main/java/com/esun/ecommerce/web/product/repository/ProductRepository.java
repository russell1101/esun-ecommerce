package com.esun.ecommerce.web.product.repository;

import com.esun.ecommerce.core.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByProductId(String productId);

    boolean existsByProductId(String productId);

    // 前台：上架且有庫存的商品
    List<Product> findByStatusAndQuantityGreaterThanOrderByInptimeDesc(Byte status, Integer quantity);

    // 批次查詢
    List<Product> findByProductIdIn(Collection<String> productIds);
}
