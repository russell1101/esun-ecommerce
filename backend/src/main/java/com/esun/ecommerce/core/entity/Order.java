package com.esun.ecommerce.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_sn")
    private Integer orderSn;

    @Column(name = "order_id", unique = true, nullable = false, length = 20)
    private String orderId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "pay_status", nullable = false)
    private Byte payStatus;

    @Column(name = "order_status", nullable = false)
    private Byte orderStatus;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "inptime", nullable = false, updatable = false)
    private LocalDateTime inptime;

    @Column(name = "updtime", nullable = false)
    private LocalDateTime updtime;

    @PrePersist
    protected void onCreate() {
        inptime = updtime = LocalDateTime.now();
        if (payStatus == null) payStatus = 0;
        if (orderStatus == null) orderStatus = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        updtime = LocalDateTime.now();
    }
}
