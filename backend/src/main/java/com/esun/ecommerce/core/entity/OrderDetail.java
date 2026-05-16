package com.esun.ecommerce.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_sn")
    private Integer orderItemSn;

    @Column(name = "order_sn", nullable = false)
    private Integer orderSn;

    @Column(name = "product_sn", nullable = false)
    private Integer productSn;

    @Column(name = "order_id", nullable = false, length = 20)
    private String orderId;

    @Column(name = "product_id", nullable = false, length = 10)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "stand_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal standPrice;

    @Column(name = "item_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal itemPrice;

    @Column(name = "inptime", nullable = false, updatable = false)
    private LocalDateTime inptime;

    @PrePersist
    protected void onCreate() {
        inptime = LocalDateTime.now();
    }
}
