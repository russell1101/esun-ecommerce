package com.esun.ecommerce.web.order.repository;

import com.esun.ecommerce.core.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findByOrderId(String orderId);

    List<OrderDetail> findByOrderIdIn(List<String> orderIds);
}
