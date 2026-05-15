package com.esun.ecommerce.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id", length = 10)
    private String productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "status", nullable = false)
    private Byte status;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "inptime", nullable = false, updatable = false)
    private LocalDateTime inptime;

    @Column(name = "updtime", nullable = false)
    private LocalDateTime updtime;

    @PrePersist
    protected void onCreate() {
        inptime = updtime = LocalDateTime.now();
        if (status == null) {
            status = 1;
        }
        if (version == null) {
            version = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updtime = LocalDateTime.now();
    }
}
