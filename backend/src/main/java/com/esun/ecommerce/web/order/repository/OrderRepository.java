package com.esun.ecommerce.web.order.repository;

import com.esun.ecommerce.core.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByMemberIdOrderByInptimeDesc(Integer memberId);

    Optional<Order> findByOrderId(String orderId);

    List<Order> findAllByOrderByInptimeDesc();
}
